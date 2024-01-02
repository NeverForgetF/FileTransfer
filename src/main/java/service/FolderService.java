package service;

import dao.FolderDao;
import entity.Folder;

import java.sql.Timestamp;
import java.util.List;

public class FolderService {
    FolderDao folderDao=new FolderDao();
    public List<Folder> getFolderAll(String sql){
        return folderDao.getFolderAll(sql);
    }
    public void insertFolder(String sql, String fileName, Timestamp saveDate){
        folderDao.insertFolder(sql, fileName, saveDate);
    }
    public void updateFolder(String sql, String fileName, Timestamp saveDate, String id){
        folderDao.updateFolder(sql, fileName, saveDate, id);
    }
}
