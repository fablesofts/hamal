package com.fable.hamal.ftp.util;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.zip.GZIPOutputStream;

import org.apache.commons.compress.archivers.ArchiveEntry;
import org.apache.commons.compress.archivers.ArchiveException;
import org.apache.commons.compress.archivers.ArchiveOutputStream;
import org.apache.commons.compress.archivers.ArchiveStreamFactory;
import org.apache.commons.compress.archivers.tar.TarArchiveEntry;
import org.apache.commons.compress.archivers.tar.TarArchiveInputStream;
import org.apache.commons.compress.archivers.tar.TarArchiveOutputStream;
import org.apache.commons.compress.archivers.zip.ZipArchiveEntry;
import org.apache.commons.compress.archivers.zip.ZipArchiveInputStream;
import org.apache.commons.compress.archivers.zip.ZipArchiveOutputStream;
import org.apache.commons.compress.compressors.gzip.GzipCompressorInputStream;
import org.apache.commons.compress.utils.IOUtils;
import org.apache.commons.io.FileUtils;
/**
 * 打包工具类
 * @modify by rdongxie
 * @author 邱爽
 */
public class GZIPUtil {
	
	private static final byte[] buf = new byte[1024 * 64];
	
	public static void main(String[] args) {
		GZIPUtil.compress(new String[]{"D:\\1.txt","D:\\3.txt","D:\\2.txt"}, "E:\\test.zip");
//		try {
//			FileUtils.moveFile(new File("D:\\1.txt"), new File("D:\\aaaaaaaaaaaaaaa\\1.txt"));
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
		
//		System.out.println(File.separator);
//		String xx = "D:\\aaaaaaaaaaaaaaa\\1.txt";
//		String x = new File(xx).getAbsolutePath();
//		System.out.println(x.substring(x.lastIndexOf(File.separator) + 1));
	}

	public static void compress(Set<FilePair> files, String tarball) {
		try {
			FileOutputStream fos = new FileOutputStream(tarball);
			ArchiveOutputStream aos = new ArchiveStreamFactory().createArchiveOutputStream("zip", fos);
			if (aos instanceof ZipArchiveOutputStream) {
                ZipArchiveOutputStream zipOut = (ZipArchiveOutputStream)aos;
                InputStream inputdata = null;
                Iterator<FilePair> iter = files.iterator();
                while (iter.hasNext()) {
                	FilePair fp = iter.next();
                    File f = new File(fp.getSource());
                    inputdata = new FileInputStream(f);
                    ZipArchiveEntry zipEntry = new ZipArchiveEntry(f, fp.getTarget());
                    zipOut.putArchiveEntry(zipEntry);
                    for (int iRead = inputdata.read(buf); iRead >= 0; iRead = inputdata.read(buf)) {
                        zipOut.write(buf, 0, iRead);
                    }
                    zipOut.flush();
                    inputdata.close();
                }
                zipOut.closeArchiveEntry();
                zipOut.finish();
                zipOut.close();
                aos.flush();
                aos.close();
                fos.flush();
                fos.close();
            }
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (ArchiveException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
	/**
     * 打包
     * @param filePath
     * @param traget
     */
    public static void compress(String[] filePath, String traget) {
    	System.setProperty("file.encoding","UTF-8");
        try {
        	
            FileOutputStream fou = new FileOutputStream(traget);
            ArchiveOutputStream archOuts =
                new ArchiveStreamFactory().createArchiveOutputStream("zip",
                    fou);
            if (archOuts instanceof ZipArchiveOutputStream) {
                ZipArchiveOutputStream zipOut =
                    (ZipArchiveOutputStream)archOuts;

                InputStream inputdata = null;
                for (int i = 0; i < filePath.length; i++) {
                    File f = new File(filePath[i]);
                    inputdata = new FileInputStream(f);
                    ZipArchiveEntry zipEntry =
                        new ZipArchiveEntry(f, "E:\\qq"+File.separator + f.getName());
                    zipOut.putArchiveEntry(zipEntry);
                    for (int iRead = inputdata.read(buf); iRead >= 0; iRead =
                        inputdata.read(buf)) {
                        zipOut.write(buf, 0, iRead);
                    }
                    zipOut.flush();
                    inputdata.close();
                }
                zipOut.closeArchiveEntry();
                zipOut.finish();
                zipOut.close();
                archOuts.flush();
                archOuts.close();
                fou.flush();
                fou.close();
            }
        }
        catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        catch (ArchiveException e) {
            e.printStackTrace();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }


    /**
     * 把zip文件解压到指定的文件夹
     * 
     * @param zipFilePath
     *        zip文件路径, 如 "D:/test/aa.zip"
     * @param saveFileDir
     *        解压后的文件存放路径, 如"D:/test/"
     */
    public static void decompressZip(String zipFilePath, String saveFileDir) {
        if (isEndsWithZip(zipFilePath)) {
            File file = new File(zipFilePath);
            if (file.exists()) {
                InputStream is = null;
                // can read Zip archives
                ZipArchiveInputStream zais = null;
                try {
                    is = new FileInputStream(file);
                    zais = new ZipArchiveInputStream(is);
                    ArchiveEntry archiveEntry = null;
                    // 把zip包中的每个文件读取出来
                    // 然后把文件写到指定的文件夹
                    while ((archiveEntry = zais.getNextEntry()) != null) {
                        // 获取文件名
                        String entryFileName = archiveEntry.getName();
                        // 构造解压出来的文件存放路径
                        String entryFilePath = saveFileDir + entryFileName;
//                        byte[] content =
//                            new byte[(int)archiveEntry.getSize()];
//                        zais.read(content);
                        OutputStream os = null;
                        try {
                            // 把解压出来的文件写到指定路径
                            File entryFile = new File(entryFilePath);
                            System.out.println(entryFilePath);
                            os = new FileOutputStream(entryFile);
                            
//                            os.write(content);
                            for (int iRead = zais.read(buf); iRead >= 0; iRead =
                                            zais.read(buf)) {
                                os.write(buf, 0, iRead);
                            }
                            os.flush();
                        }
                        catch (IOException e) {
                            throw new IOException(e);
                        }
                        finally {
                            if (os != null) {
                                os.flush();
                                os.close();
                            }
                        }

                    }
                }
                catch (Exception e) {
                    throw new RuntimeException(e);
                }
                finally {
                    try {
                        if (zais != null) {
                            zais.close();
                        }
                        if (is != null) {
                            is.close();
                        }
                    }
                    catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        }
    }


    /**
     * 判断文件名是否以.zip为后缀
     * 
     * @param fileName
     *        需要判断的文件名
     * @return 是zip文件返回true,否则返回false
     */
    public static boolean isEndsWithZip(String fileName) {
        boolean flag = false;
        if (fileName != null && !"".equals(fileName.trim())) {
            if (fileName.endsWith(".ZIP") || fileName.endsWith(".zip")) {
                flag = true;
            }
        }
        return flag;
    }


    
	/**
	 * 文件打成tar包
	 * @param source 要打包的原文件
	 * @param target 打包后的文件
	 * @return 返回打包后的文件
	 * @throws IOException 
	 */
	public static File pack(File[]sources,File target) throws IOException {
		FileOutputStream out = null;
		try {
			out = new FileOutputStream(target);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		TarArchiveOutputStream os = new TarArchiveOutputStream(out);
		for (File file : sources) {
			try {
				os.putArchiveEntry(new TarArchiveEntry(file));
				IOUtils.copy(new FileInputStream(file), os);
				os.closeArchiveEntry();
			} catch (FileNotFoundException e) {
				throw new FileNotFoundException();
			} catch (IOException e) {
				throw new IOException(e);
			}
		}
		if(os != null) {
			try {
				os.flush();
				os.close();
			} catch (IOException e) {
				throw new IOException(e);
			}
		}
		return target;
	}
	
	/**
	 * 只能解压成tmp.tar
	 * @param file
	 * @return
	 */
	public static File deCompress(File file) {
		FileOutputStream out = null;
		GzipCompressorInputStream gzIn = null;
		try {
			FileInputStream fin = new FileInputStream(file);
			BufferedInputStream in = new BufferedInputStream(fin);
			File outFile = new File(file.getParent() + File.separator +"tmp.tar");
			out = new FileOutputStream(outFile);
			gzIn = new GzipCompressorInputStream(in);
			final byte[] buffer = new byte[2048];
			int n = 0;
			while (-1 !=(n = gzIn.read(buffer))) {
				out.write(buffer, 0 , n);
			}
			return outFile;
		} catch (Exception e) {
			return null;
		} finally {
			try {
				out.close();
				gzIn.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	/**
	 * 解压成tar包
	 * @param file
	 */
	public static void deCompressTARFile(File file) {
		String basePath = file.getParent() + File.separator;
		TarArchiveInputStream is = null;
		try {
			is = new TarArchiveInputStream(new FileInputStream(file));
			while(true) {
				TarArchiveEntry entry = is.getNextTarEntry();
				if (entry == null) {
					break;
				}
				if (entry.isDirectory()) {
					new File(basePath + entry.getName()).mkdirs();
				} else {
					FileOutputStream os = null;
					try {
						File f = new File(basePath + entry.getName());
						if (!f.getParentFile().exists()) {
							f.getParentFile().mkdirs();
						}
						if (!f.exists()) {
							f.createNewFile();
						}
						os = new FileOutputStream(f);
						byte[] bs= new byte[2048];
						int len = -1;
						while ((len = is.read(bs))!=-1) {
							os.write(bs, 0, len);
						}
						os.flush();
					} catch (FileNotFoundException e) {
						e.printStackTrace();
					} catch (IOException e) {
						e.printStackTrace();
					} finally {
						os.close();
					}
				}
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				is.close();
				file.delete();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	/**
	 * 打包成tar.gz
	 * @param source 需要压缩的文件
	 * @return File 返回压缩的文件
	 */
	public static File compress(File source) {
		//要输出的文件
		File target = new File(source.getName() + ".gz");
		FileInputStream in = null;
		GZIPOutputStream out = null;
		try {
			in = new FileInputStream(source);
			out = new GZIPOutputStream(new FileOutputStream(target));
			byte[] array = new byte[1024];
			int number = -1;
			while((number = in.read(array,0,array.length))!=-1) {
				out.write(array,0,number);
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			return null;
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		} finally {
			if(in != null) {
				try {
					in.close();
				} catch (IOException e) {
					e.printStackTrace();
					return null;
				}
			}
			if(out != null) {
				try {
					out.close();
				} catch (IOException e) {
					e.printStackTrace();
					return null;
				}
			}
		}
		return target;
	}
	
	
	public static void main1(String[] args) {
//		File[] sources= new File[] {
//				new File("src/main/java/com/fable/hamal/ftp/util/task.xml"),
//				new File("src/main/java/com/fable/hamal/ftp/util/app.properties")};
//		
//		//target不存在时，自动创建
//		File target = new File("release_package.tar");
//		try {
//			compress(pack(sources, target));
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
		
		//解压
//		deCompress(new File("d:\\release_package.tar.gz"));
//		deCompressTARFile(new File("d:\\release_package.tar"));  
		//打包  
	    GZIPUtil.compress(new String[]{"E:\\1000\\复件 (101) test.txt","E:\\1000\\复件 (104) test.txt","E:\\1000\\复件 (106) test.txt"}, "C:\\test.zip");
	      //解包
	    GZIPUtil.decompressZip("C:\\test.zip", "D:\\log\\");
	}
}
