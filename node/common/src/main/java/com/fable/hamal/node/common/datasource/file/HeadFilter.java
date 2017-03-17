package com.fable.hamal.node.common.datasource.file;

import java.io.IOException;
import java.io.PushbackInputStream;

/**
 * @author 金渊
 */
public class HeadFilter {

	private static final byte[] HEAD_OFFICE_1 = new byte[] { (byte) 0xD0,
			(byte) 0xCF, (byte) 0x11, (byte) 0xE0, (byte) 0xA1, (byte) 0xB1, };
	private static final byte[] HEAD_OFFICE_2 = new byte[] { (byte) 0x50,
			(byte) 0x4B, (byte) 0x03, (byte) 0x04, (byte) 0x14, (byte) 0x00,
			(byte) 0x06, (byte) 0x00, (byte) 0x08, };
	private static final byte[] HEAD_JPEG_1 = new byte[] { (byte) 0xff,
			(byte) 0xd8, };
	private static final byte[] HEAD_JPEG_2 = new byte[] { (byte) 0xff,
			(byte) 0xd9, };
	private static final byte[] HEAD_TGA_1 = new byte[] { (byte) 0x00,
			(byte) 0x00, (byte) 0x02, (byte) 0x00, (byte) 0x00, };
	private static final byte[] HEAD_TGA_2 = new byte[] { (byte) 0x00,
			(byte) 0x00, (byte) 0x10, (byte) 0x00, (byte) 0x00, };
	private static final byte[] HEAD_PNG = new byte[] { (byte) 0x89,
			(byte) 0x50, (byte) 0x4E, (byte) 0x47, (byte) 0x0D, (byte) 0x0A,
			(byte) 0x1A, (byte) 0x0A, };
	private static final byte[] HEAD_GIF_1 = new byte[] { (byte) 0x47,
			(byte) 0x49, (byte) 0x46, (byte) 0x38, (byte) 0x39, (byte) 0x61, };
	private static final byte[] HEAD_GIF_2 = new byte[] { (byte) 0x47,
			(byte) 0x49, (byte) 0x46, (byte) 0x38, (byte) 0x37, (byte) 0x61, };
	private static final byte[] HEAD_BMP = new byte[] { (byte) 0x42,
			(byte) 0x4D, };
	private static final byte[] HEAD_PCX = new byte[] { (byte) 0x0A, };
	private static final byte[] HEAD_TIFF_1 = new byte[] { (byte) 0x4D,
			(byte) 0x4D, };
	private static final byte[] HEAD_TIFF_2 = new byte[] { (byte) 0x49,
			(byte) 0x49, };
	private static final byte[] HEAD_ICO = new byte[] { (byte) 0x00,
			(byte) 0x00, (byte) 0x01, (byte) 0x00, (byte) 0x01, (byte) 0x00,
			(byte) 0x20, (byte) 0x20, };
	private static final byte[] HEAD_CUR = new byte[] { (byte) 0x00,
			(byte) 0x00, (byte) 0x02, (byte) 0x00, (byte) 0x01, (byte) 0x00,
			(byte) 0x20, (byte) 0x20, };
	private static final byte[] HEAD_IFF = new byte[] { (byte) 0x46,
			(byte) 0x4F, (byte) 0x52, (byte) 0x4D, };
	private static final byte[] HEAD_ANI = new byte[] { (byte) 0x52,
			(byte) 0x49, (byte) 0x46, (byte) 0x46, };

	private static final byte[] HEAD_XML_1 = new byte[] { (byte) 0x3C,
			(byte) 0x3F, (byte) 0x78, (byte) 0x6D, (byte) 0x6C };
	private static final byte[] HEAD_XML_2 = new byte[] { (byte) 0xEF,
			(byte) 0xBB, (byte) 0xBF, (byte) 0x3C, (byte) 0x3F, (byte) 0x78,
			(byte) 0x6D, (byte) 0x6C };
	private static final byte[] HEAD_RAR = new byte[] { (byte) 0x52,
			(byte) 0x61, (byte) 0x72, (byte) 0x21 };
	private static final byte[] HEAD_ZIP = new byte[] { (byte) 0x50,
			(byte) 0x4B, (byte) 0x03, (byte) 0x04 };
	private static final byte[] HEAD_EXE = new byte[] { (byte) 0x4D,
			(byte) 0x5A, (byte) 0x90, (byte) 0x00, (byte) 0x03 };

	private static final byte[][][] HEAD_FILTER = new byte[][][] {
			new byte[][] { HeadFilter.HEAD_OFFICE_1,HeadFilter.HEAD_OFFICE_2 },
			new byte[][] { HeadFilter.HEAD_JPEG_1, HeadFilter.HEAD_JPEG_2,
					HeadFilter.HEAD_TGA_1, HeadFilter.HEAD_TGA_2,
					HeadFilter.HEAD_PNG, HeadFilter.HEAD_GIF_1,
					HeadFilter.HEAD_GIF_2, HeadFilter.HEAD_BMP,
					HeadFilter.HEAD_PCX, HeadFilter.HEAD_TIFF_1,
					HeadFilter.HEAD_TIFF_2, HeadFilter.HEAD_ICO,
					HeadFilter.HEAD_CUR, HeadFilter.HEAD_IFF,
					HeadFilter.HEAD_ANI, },
			{ HeadFilter.HEAD_XML_1, HeadFilter.HEAD_XML_2 },
			{ HeadFilter.HEAD_RAR }, { HeadFilter.HEAD_ZIP },
			{ HeadFilter.HEAD_EXE } };
	public static final int HEAD_RESULT_OFFICE = 0;
	public static final int HEAD_RESULT_IMG = 1;
	public static final int HEAD_RESULT_XML = 2;
	public static final int HEAD_RESULT_RAR = 3;
	public static final int HEAD_RESULT_ZIP = 4;
	public static final int HEAD_RESULT_EXE = 5;
	private static final int MAX_HEAD;
	static {
		int iTemp = 0;
		for (final byte[][] element : HeadFilter.HEAD_FILTER)
			for (final byte[] element2 : element)
				iTemp = Math.max(iTemp, element2.length);
		MAX_HEAD = iTemp;
	}

	public static int checkHead(final byte[] head) {
		if (head == null)
			return -1;
		boolean bCheck = true;
		for (int i = 0, iLoop_i = HeadFilter.HEAD_FILTER.length; i < iLoop_i; i++)
			for (final byte[] impress : HeadFilter.HEAD_FILTER[i]) {
				bCheck = true;
				if (head.length < impress.length)
					continue;
				for (int j = 0, iLoop_j = impress.length; j < iLoop_j; j++)
					if (head[j] != impress[j]) {
						bCheck = false;
						break;
					}
				if (bCheck)
					return i;
			}
		return -1;
	}

	public static int checkHead(final PushbackInputStream in)
			throws IOException {
		if (in == null)
			return -1;
		final byte[] buf = new byte[HeadFilter.MAX_HEAD];
		int iTemp = 0;
		int i = 0;
		for (; i < HeadFilter.MAX_HEAD; i++) {
			iTemp = in.read();
			if (iTemp < 0)
				break;
			buf[i] = (byte) iTemp;
		}
		final byte[] head = new byte[i];
		System.arraycopy(buf, 0, head, 0, i);
		in.unread(head, 0, i);
		return HeadFilter.checkHead(head);
	}
}
