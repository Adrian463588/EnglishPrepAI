package com.englishprep.ai.data.repository

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.StatFs
import android.provider.OpenableColumns
import androidx.documentfile.provider.DocumentFile
import com.englishprep.ai.data.datastore.UserPreferencesRepository
import com.englishprep.ai.data.imports.SafSecurityValidator
import com.englishprep.ai.data.room.AssetSeeder
import com.englishprep.ai.data.room.CatalogDao
import com.englishprep.ai.data.room.ChatAndModelDao
import com.englishprep.ai.data.room.ChatMessageEntity
import com.englishprep.ai.data.room.ChatThreadEntity
import com.englishprep.ai.data.room.ImportJobEntity
import com.englishprep.ai.data.room.ModelInstallEntity
import com.englishprep.ai.data.room.PackDocumentDto
import com.englishprep.ai.domain.engines.LocalLlmEngine
import com.englishprep.ai.domain.engines.SpeechToTextEngine
import com.englishprep.ai.domain.models.ExamId
import com.englishprep.ai.domain.repositories.AiAndImportRepository
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.io.ByteArrayInputStream
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.util.UUID
import java.util.zip.ZipInputStream
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AiAndImportRepositoryImpl @Inject constructor(
    @ApplicationContext private val context: Context,
    private val chatAndModelDao: ChatAndModelDao,
    private val catalogDao: CatalogDao,
    private val preferences: UserPreferencesRepository,
    private val validator: SafSecurityValidator,
    private val llmEngine: LocalLlmEngine,
    private val sttEngine: SpeechToTextEngine,
    private val json: Json,
) : AiAndImportRepository {

    override fun observeThreads(): Flow<List<ChatThreadEntity>> = chatAndModelDao.observeThreads()

    override fun observeMessages(threadId: String): Flow<List<ChatMessageEntity>> =
        chatAndModelDao.observeMessages(threadId)

    override fun observeInstalledModels(): Flow<List<ModelInstallEntity>> =
        chatAndModelDao.observeInstalledModels()

    override fun observeImportJobs(): Flow<List<ImportJobEntity>> =
        chatAndModelDao.observeImportJobs()

    override suspend fun getOrCreateThread(examId: ExamId?): ChatThreadEntity {
        val existing = chatAndModelDao.observeThreads().first()
            .firstOrNull { it.examId == examId?.name }
        if (existing != null) return existing
        val now = System.currentTimeMillis()
        val created = ChatThreadEntity(
            id = "THR_${examId?.name ?: "GENERAL"}",
            examId = examId?.name,
            title = "Tutor ${examId?.title ?: "Umum"}",
            createdAt = now,
            updatedAt = now,
        )
        chatAndModelDao.upsertThread(created)
        return created
    }

    override suspend fun appendMessage(
        threadId: String,
        role: String,
        text: String,
        citations: List<String>,
        modelId: String?,
    ) {
        val now = System.currentTimeMillis()
        chatAndModelDao.insertMessage(
            ChatMessageEntity(
                id = "MSG_${UUID.randomUUID()}",
                threadId = threadId,
                role = role,
                text = text,
                citationsJson = if (citations.isEmpty()) null else json.encodeToString(citations),
                modelId = modelId,
                createdAt = now,
            ),
        )
    }

    override suspend fun importModelFileViaSaf(
        uriString: String,
        expectedFamily: String,
    ): Result<ModelInstallEntity> = withContext(Dispatchers.IO) {
        val jobId = "IMP_${UUID.randomUUID()}"
        val now = System.currentTimeMillis()
        chatAndModelDao.upsertImportJob(
            ImportJobEntity(jobId, "MODEL_$expectedFamily", uriString, "RUNNING", null, now, null),
        )

        runCatching {
            val uri = Uri.parse(uriString)
            persistReadGrant(uri)

            val (name, size) = queryDisplayNameAndSize(uri)
            val header = context.contentResolver.openInputStream(uri)?.use { stream ->
                val buf = ByteArray(64)
                val n = stream.read(buf)
                if (n <= 0) ByteArray(0) else buf.copyOf(n)
            } ?: error("Izin akses berkas SAF dicabut atau berkas tidak dapat dibaca.")

            val freeBytes = StatFs(context.filesDir.absolutePath).availableBytes
            val check = validator.validateModelHeader(name, size, header, freeBytes)
            require(check.isValid) { check.reason }

            val modelId = "MDL_${expectedFamily}_${UUID.randomUUID().toString().take(8)}"
            val destDir = File(context.filesDir, "models/$modelId").apply { mkdirs() }
            val tmpFile = File(destDir, "$name.tmp")
            val finalFile = File(destDir, name)

            context.contentResolver.openInputStream(uri)?.use { input ->
                FileOutputStream(tmpFile).use { output -> input.copyTo(output, 65_536) }
            } ?: error("Gagal membaca stream SAF.")

            if (!tmpFile.renameTo(finalFile)) {
                tmpFile.copyTo(finalFile, overwrite = true)
                tmpFile.delete()
            }

            val sha = FileInputStream(finalFile).use { validator.sha256Hex(it) }
            val entity = ModelInstallEntity(
                id = modelId,
                displayName = name,
                family = expectedFamily,
                modelVariant = name.substringBeforeLast('.'),
                runtime = "LiteRT-LM 0.13.1",
                format = ".litertlm",
                quantization = "mobile-quant",
                sha256 = sha,
                sizeBytes = finalFile.length(),
                privateStoragePath = finalFile.absolutePath,
                sourceDocumentUri = uriString,
                status = "READY",
                installedAt = System.currentTimeMillis(),
                licenseId = "Gemma-Terms",
            )
            chatAndModelDao.upsertModel(entity)
            preferences.setActiveLlmModel(entity.id)
            llmEngine.load(entity.privateStoragePath, entity.displayName, entity.modelVariant)
            chatAndModelDao.upsertImportJob(
                ImportJobEntity(jobId, "MODEL_$expectedFamily", uriString, "COMPLETED", null, now, System.currentTimeMillis()),
            )
            entity
        }.onFailure { err ->
            chatAndModelDao.upsertImportJob(
                ImportJobEntity(jobId, "MODEL_$expectedFamily", uriString, "FAILED", err.message, now, System.currentTimeMillis()),
            )
        }
    }

    override suspend fun importWhisperFolderViaSaf(treeUriString: String): Result<ModelInstallEntity> =
        withContext(Dispatchers.IO) {
            val jobId = "IMP_${UUID.randomUUID()}"
            val now = System.currentTimeMillis()
            chatAndModelDao.upsertImportJob(
                ImportJobEntity(jobId, "MODEL_WHISPER_ONNX", treeUriString, "RUNNING", null, now, null),
            )
            runCatching {
                val treeUri = Uri.parse(treeUriString)
                persistReadGrant(treeUri)
                val rootDoc = DocumentFile.fromTreeUri(context, treeUri)
                    ?: error("Folder SAF tidak dapat diakses atau izin telah dicabut.")

                val children = rootDoc.listFiles()
                val encoder = children.firstOrNull {
                    (it.name ?: "").contains("encoder", true) && (it.name ?: "").endsWith(".onnx", true)
                }
                val decoder = children.firstOrNull {
                    (it.name ?: "").contains("decoder", true) && (it.name ?: "").endsWith(".onnx", true)
                }
                val vocab = children.firstOrNull {
                    val n = it.name.orEmpty()
                    n.equals("vocab.json", true) || n.endsWith("tokens.txt", true)
                }

                require(encoder != null && decoder != null && vocab != null) {
                    "Folder Whisper ONNX wajib memuat encoder*.onnx, decoder*.onnx, dan vocab.json (atau tokens.txt)."
                }

                val neededBytes = listOf(encoder, decoder, vocab).sumOf { it.length() }
                val freeBytes = StatFs(context.filesDir.absolutePath).availableBytes
                require(freeBytes > neededBytes + 128L * 1024L * 1024L) {
                    "Ruang penyimpanan tidak mencukupi untuk menyalin Whisper ONNX."
                }

                val modelId = "MDL_WHISPER_${UUID.randomUUID().toString().take(8)}"
                val destDir = File(context.filesDir, "models/$modelId").apply { mkdirs() }
                listOf(encoder, decoder, vocab).forEach { doc ->
                    val fileName = doc.name ?: error("Nama berkas tidak valid")
                    val out = File(destDir, fileName)
                    context.contentResolver.openInputStream(doc.uri)?.use { input ->
                        FileOutputStream(out).use { output -> input.copyTo(output, 32_768) }
                    } ?: error("Gagal membaca berkas $fileName")
                }

                val encFile = File(destDir, encoder.name!!)
                val sha = FileInputStream(encFile).use { validator.sha256Hex(it) }
                val entity = ModelInstallEntity(
                    id = modelId,
                    displayName = rootDoc.name ?: "Whisper ONNX",
                    family = "WHISPER_ONNX",
                    modelVariant = "whisper-onnx",
                    runtime = "ONNX Runtime 1.22.0",
                    format = ".onnx",
                    quantization = "onnx",
                    sha256 = sha,
                    sizeBytes = neededBytes,
                    privateStoragePath = destDir.absolutePath,
                    sourceDocumentUri = treeUriString,
                    status = "READY",
                    installedAt = System.currentTimeMillis(),
                    licenseId = "MIT/Apache-2.0",
                )
                chatAndModelDao.upsertModel(entity)
                preferences.setActiveSttModel(entity.id)
                sttEngine.load(destDir.absolutePath, entity.displayName)
                chatAndModelDao.upsertImportJob(
                    ImportJobEntity(jobId, "MODEL_WHISPER_ONNX", treeUriString, "COMPLETED", null, now, System.currentTimeMillis()),
                )
                entity
            }.onFailure { err ->
                chatAndModelDao.upsertImportJob(
                    ImportJobEntity(jobId, "MODEL_WHISPER_ONNX", treeUriString, "FAILED", err.message, now, System.currentTimeMillis()),
                )
            }
        }

    override suspend fun importStudyPackViaSaf(uriString: String): Result<String> = withContext(Dispatchers.IO) {
        val jobId = "IMP_${UUID.randomUUID()}"
        val now = System.currentTimeMillis()
        chatAndModelDao.upsertImportJob(ImportJobEntity(jobId, "STUDY_PACK", uriString, "RUNNING", null, now, null))
        runCatching {
            val uri = Uri.parse(uriString)
            persistReadGrant(uri)
            val (name, size) = queryDisplayNameAndSize(uri)
            require(size > 0L) { "Berkas paket kosong (0-byte)." }

            if (name.endsWith(".zip", ignoreCase = true)) {
                val rawBytes = context.contentResolver.openInputStream(uri)?.use { it.readBytes() }
                    ?: error("Izin SAF dicabut atau arsip gagal dibuka.")
                val safety = validator.inspectZipSafety(ByteArrayInputStream(rawBytes))
                require(safety.isValid) { safety.reason }

                val packDir = File(context.filesDir, "packs/${UUID.randomUUID().toString().take(8)}").apply { mkdirs() }
                var manifestDoc: PackDocumentDto? = null
                ZipInputStream(ByteArrayInputStream(rawBytes)).use { zis ->
                    var entry = zis.nextEntry
                    while (entry != null) {
                        if (!entry.isDirectory) {
                            val cleanName = entry.name.trimStart('/')
                            val outFile = File(packDir, cleanName)
                            require(outFile.canonicalPath.startsWith(packDir.canonicalPath)) { "Jalur arsip tidak sah." }
                            outFile.parentFile?.mkdirs()
                            FileOutputStream(outFile).use { fos -> zis.copyTo(fos) }
                            if (cleanName.endsWith(".json", true) && !cleanName.equals("index.json", true)) {
                                manifestDoc = json.decodeFromString<PackDocumentDto>(outFile.readText())
                            }
                        }
                        zis.closeEntry()
                        entry = zis.nextEntry
                    }
                }
                val doc = manifestDoc ?: error("Arsip ZIP tidak berisi manifest paket soal JSON.")
                AssetSeeder.installParsedPack(
                    doc = doc,
                    installedPath = packDir.absolutePath,
                    importedAt = now,
                    audioBytes = size,
                    catalogDao = catalogDao,
                    json = json,
                    assetUriPrefix = "${packDir.absolutePath}/",
                )
                chatAndModelDao.upsertImportJob(ImportJobEntity(jobId, "STUDY_PACK", uriString, "COMPLETED", null, now, System.currentTimeMillis()))
                doc.title
            } else {
                val text = context.contentResolver.openInputStream(uri)?.bufferedReader()?.use { it.readText() }
                    ?: error("Gagal membaca berkas JSON paket soal.")
                val doc = json.decodeFromString<PackDocumentDto>(text)
                require(doc.items.isNotEmpty()) { "Paket soal tidak memiliki butir pertanyaan." }
                val packDir = File(context.filesDir, "packs/${doc.packId}").apply { mkdirs() }
                File(packDir, "${doc.packId}.json").writeText(text)
                AssetSeeder.installParsedPack(
                    doc = doc,
                    installedPath = packDir.absolutePath,
                    importedAt = now,
                    audioBytes = 0L,
                    catalogDao = catalogDao,
                    json = json,
                    assetUriPrefix = "asset://",
                )
                chatAndModelDao.upsertImportJob(ImportJobEntity(jobId, "STUDY_PACK", uriString, "COMPLETED", null, now, System.currentTimeMillis()))
                doc.title
            }
        }.onFailure { err ->
            chatAndModelDao.upsertImportJob(ImportJobEntity(jobId, "STUDY_PACK", uriString, "FAILED", err.message, now, System.currentTimeMillis()))
        }
    }

    override suspend fun activateModel(model: ModelInstallEntity): Result<Unit> {
        if (!verifyInstalledModelIntegrity(model)) {
            return Result.failure(IllegalStateException("Berkas lokal model '${model.displayName}' hilang atau korup. Silakan impor ulang."))
        }
        return if (model.family == "WHISPER_ONNX") {
            preferences.setActiveSttModel(model.id)
            sttEngine.load(model.privateStoragePath, model.displayName)
        } else {
            preferences.setActiveLlmModel(model.id)
            llmEngine.load(model.privateStoragePath, model.displayName, model.modelVariant)
        }
    }

    override suspend fun removeModel(modelId: String) {
        val model = chatAndModelDao.getModel(modelId) ?: return
        if (model.family == "WHISPER_ONNX") {
            sttEngine.unload()
            preferences.setActiveSttModel(null)
        } else {
            llmEngine.unload()
            preferences.setActiveLlmModel(null)
        }
        runCatching {
            val target = File(model.privateStoragePath)
            val parentModelDir = if (target.isDirectory) target else target.parentFile
            parentModelDir?.deleteRecursively()
        }
        chatAndModelDao.deleteModel(modelId)
    }

    override suspend fun verifyInstalledModelIntegrity(model: ModelInstallEntity): Boolean {
        val f = File(model.privateStoragePath)
        return f.exists() && (f.isDirectory || f.length() > 0L)
    }

    private fun persistReadGrant(uri: Uri) {
        runCatching {
            context.contentResolver.takePersistableUriPermission(
                uri,
                Intent.FLAG_GRANT_READ_URI_PERMISSION,
            )
        }
    }

    private fun queryDisplayNameAndSize(uri: Uri): Pair<String, Long> {
        var displayName = uri.lastPathSegment?.substringAfterLast('/') ?: "imported_file"
        var size = -1L
        context.contentResolver.query(uri, arrayOf(OpenableColumns.DISPLAY_NAME, OpenableColumns.SIZE), null, null, null)?.use { c ->
            if (c.moveToFirst()) {
                val nIdx = c.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                val sIdx = c.getColumnIndex(OpenableColumns.SIZE)
                if (nIdx >= 0) displayName = c.getString(nIdx) ?: displayName
                if (sIdx >= 0 && !c.isNull(sIdx)) size = c.getLong(sIdx)
            }
        }
        if (size < 0L) {
            size = runCatching {
                context.contentResolver.openAssetFileDescriptor(uri, "r")?.use { it.length } ?: 0L
            }.getOrDefault(0L)
        }
        return displayName to size
    }
}
