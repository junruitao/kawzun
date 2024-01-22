package com.kwz.test;

import java.io.IOException;

import org.junit.Test;

import com.kwz.service.KwzDao;
import com.kwz.service.KwzMockDaoImpl;
import com.kwz.service.KwzS3DaoImpl;

public class S3Test {
    @Test
    public void save() throws IOException {
        KwzS3DaoImpl dao = new KwzS3DaoImpl();
        KwzDao mockDao = new KwzMockDaoImpl();
        dao.setCatalogs(mockDao.getCatalogs());
        dao.setUsers(mockDao.getUsers());
        dao.setNews(mockDao.getNews());
        dao.flush(true);
        dao.load();
    }
}
