package dao;

import entity.Folder;

import java.sql.Timestamp;
import java.util.List;

public class FolderDao {
    public List<Folder> getFolderAll(String sql){
        return jdbc2.selectList(Folder.class,sql);
    }
    public void insertFolder(String sql, String fileName, Timestamp saveDate){
        jdbc2.doInsert(jdbc2.connection, sql, fileName, saveDate);
    }
    public void updateFolder(String sql, String fileName, Timestamp saveDate, String id){
        jdbc2.doUpdate(jdbc2.connection, sql, fileName, saveDate, id);
    }
}
