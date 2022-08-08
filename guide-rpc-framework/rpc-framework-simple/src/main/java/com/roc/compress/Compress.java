package com.roc.compress;

import com.roc.extension.SPI;

/**
 * @Description Compress
 * @Author dongp
 * @Date 2022/8/8 0008 15:49
 */
@SPI
public interface Compress {

    byte[] compress(byte[] bytes);

    byte[] decompress(byte[] bytes);
}
