package br.com.prodasiq.leopardoa7printer.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.CharacterCodingException;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CharsetEncoder;

public class FileHelper {

    public static String getFileContent(String path) {
        StringBuilder content = new StringBuilder();
        BufferedReader br = null;

        try {
            String sCurrentLine;
            br = new BufferedReader(new InputStreamReader(new FileInputStream(path)));

            while ((sCurrentLine = br.readLine()) != null) {
                content.append(sCurrentLine);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (br != null) br.close();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }

        return content.toString();
    }

    public static byte[] getFileBytes(String path) {
        FileInputStream fileInputStream = null;
        File file = new File(path);

        byte[] bFile = new byte[(int) file.length()];

        try {
            //convert file into array of bytes
            fileInputStream = new FileInputStream(file);
            fileInputStream.read(bFile);
            fileInputStream.close();
        }catch(Exception e){
            e.printStackTrace();
        }

        return bFile;
    }

    public static byte[] decodeText(String text, String encoding) throws CharacterCodingException, UnsupportedEncodingException {
        Charset charset = Charset.forName(encoding);
        CharsetDecoder decoder = charset.newDecoder();
        CharsetEncoder encoder = charset.newEncoder();
        ByteBuffer bbuf = encoder.encode(CharBuffer.wrap(text));
        CharBuffer cbuf = decoder.decode(bbuf);
        String s = cbuf.toString();
        return s.getBytes(encoding);
    }
}
