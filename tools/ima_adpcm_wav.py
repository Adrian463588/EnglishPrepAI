"""Pure Python MS IMA-ADPCM (0x0011) 16 kHz mono RIFF WAV encoder aligned with Media3 WavExtractor."""

import struct

IMA_INDEX_TABLE = [
    -1, -1, -1, -1, 2, 4, 6, 8,
    -1, -1, -1, -1, 2, 4, 6, 8,
]

IMA_STEP_TABLE = [
    7, 8, 9, 10, 11, 12, 13, 14, 16, 17, 19, 21, 23, 25, 28, 31,
    34, 37, 41, 45, 50, 55, 60, 66, 73, 80, 88, 97, 107, 118, 130, 143,
    157, 173, 190, 209, 230, 253, 279, 307, 337, 371, 408, 449, 494, 544, 598, 658,
    724, 796, 876, 963, 1060, 1166, 1282, 1411, 1552, 1707, 1878, 2066, 2272, 2499, 2749, 3024,
    3327, 3660, 4026, 4428, 4871, 5358, 5894, 6484, 7132, 7845, 8630, 9493, 10442, 11487, 12635, 13899,
    15289, 16818, 18500, 20350, 22385, 24623, 27086, 29794, 32767,
]

BLOCK_ALIGN = 256
SAMPLES_PER_BLOCK = ((BLOCK_ALIGN - 4) * 8) // 4 + 1  # 505


def _encode_nibble(sample: int, predictor: int, step_index: int) -> tuple[int, int, int]:
    step = IMA_STEP_TABLE[step_index]
    diff = sample - predictor
    nibble = 0
    if diff < 0:
        nibble = 8
        diff = -diff
    delta = step >> 3
    if diff >= step:
        nibble |= 4
        diff -= step
        delta += step
    step >>= 1
    if diff >= step:
        nibble |= 2
        diff -= step
        delta += step
    step >>= 1
    if diff >= step:
        nibble |= 1
        delta += step
    if nibble & 8:
        predictor -= delta
    else:
        predictor += delta
    if predictor > 32767:
        predictor = 32767
    elif predictor < -32768:
        predictor = -32768
    step_index += IMA_INDEX_TABLE[nibble]
    if step_index < 0:
        step_index = 0
    elif step_index > 88:
        step_index = 88
    return nibble & 0x0F, predictor, step_index


def encode_pcm16_to_ima_adpcm_wav(pcm_bytes: bytes, sample_rate: int = 16000) -> bytes:
    """Encodes raw 16-bit little-endian mono PCM frames into a RIFF IMA-ADPCM WAV."""
    total_samples = len(pcm_bytes) // 2
    if total_samples == 0:
        samples = [0] * SAMPLES_PER_BLOCK
    else:
        samples = list(struct.unpack(f"<{total_samples}h", pcm_bytes[: total_samples * 2]))
        rem = len(samples) % SAMPLES_PER_BLOCK
        if rem != 0:
            samples.extend([0] * (SAMPLES_PER_BLOCK - rem))

    blocks = bytearray()
    step_index = 0
    for offset in range(0, len(samples), SAMPLES_PER_BLOCK):
        blk = samples[offset : offset + SAMPLES_PER_BLOCK]
        predictor = blk[0]
        blocks.extend(struct.pack("<hBB", predictor, step_index, 0))
        nib_buf = bytearray(BLOCK_ALIGN - 4)
        for i in range(1, SAMPLES_PER_BLOCK):
            nib, predictor, step_index = _encode_nibble(blk[i], predictor, step_index)
            idx = i - 1
            byte_pos = idx >> 1
            if (idx & 1) == 0:
                nib_buf[byte_pos] = nib
            else:
                nib_buf[byte_pos] |= nib << 4
        blocks.extend(nib_buf)

    avg_bytes_per_sec = (sample_rate * BLOCK_ALIGN) // SAMPLES_PER_BLOCK
    fmt_chunk = struct.pack(
        "<4sIHHIIHHHH",
        b"fmt ",
        20,
        0x0011,  # WAVE_FORMAT_DVI_ADPCM
        1,
        sample_rate,
        avg_bytes_per_sec,
        BLOCK_ALIGN,
        4,
        2,
        SAMPLES_PER_BLOCK,
    )
    fact_chunk = struct.pack("<4sII", b"fact", 4, len(samples))
    data_bytes = bytes(blocks)
    data_header = struct.pack("<4sI", b"data", len(data_bytes))
    riff_size = 4 + len(fmt_chunk) + len(fact_chunk) + len(data_header) + len(data_bytes)
    riff_header = struct.pack("<4sI4s", b"RIFF", riff_size, b"WAVE")
    return riff_header + fmt_chunk + fact_chunk + data_header + data_bytes
