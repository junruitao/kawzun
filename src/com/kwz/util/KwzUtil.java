package com.kwz.util;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

import org.apache.commons.lang.SerializationUtils;
import org.apache.commons.lang.StringUtils;

import com.kwz.entity.KwzBaseBean;
import com.kwz.enums.EntityType;

public class KwzUtil {
    static public String textToHtml(String txt) {
        if (txt != null) {
            String ret = StringUtils.replace(StringUtils.replace(txt, "\r", "<br/>"), "\n", "<br/>");
            return ret;
        }
        return null;
    }

    static public String dateToString(Date date) {
        SimpleDateFormat fmt = new SimpleDateFormat("YY-mm-dd");
        return fmt.format(date);
    }

    static public byte[] getBytes(EntityType type, Serializable kwzData) throws IOException {
        byte[] data = SerializationUtils.serialize(kwzData);
        System.out.println("Ser size" + data.length);
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        ZipOutputStream zipfile = new ZipOutputStream(bos);
        String fileName = type.name();
        ZipEntry zipentry = null;
        zipentry = new ZipEntry(fileName);
        zipfile.putNextEntry(zipentry);
        zipfile.write(data);
        zipfile.close();
        data = bos.toByteArray();
        bos.close();
        return data;
    }

    static public Object getEntities(EntityType type, InputStream is) throws IOException {
        Object ret = null;
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        ZipInputStream zipfileIn = new ZipInputStream(is);
        ZipEntry entry;
        while ((entry = zipfileIn.getNextEntry()) != null) {
            System.out.println(entry.getName() + "" + entry.getSize());
            if (entry.getName().equals(type.name())) {
                byte[] buffer = new byte[1024];
                int len;
                bos.reset();
                while ((len = zipfileIn.read(buffer)) > 0)
                    bos.write(buffer, 0, len);
                ret = SerializationUtils.deserialize(bos.toByteArray());
                break;
            }
        }
        is.close();
        return ret;
    }

    static public <T extends KwzBaseBean> void reindex(List<T> list) {
        // if (list == null)
        // return;
        // int i = 0;
        // for (IndexedIdedTimestampedBase o : list) {
        // o.setIndex(i++);
        // }
    }
}
