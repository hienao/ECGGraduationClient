package cn.edu.tjut.ecggraduationproject.model;

/**
 * Created by Administrator on 2016/4/23 0023.
 */
public class LocalFileInfo {
    private long time;
    private String name;
    private String path;

    public LocalFileInfo(long time, String name, String path) {
        this.time = time;
        this.name = name;
        this.path = path;
    }
    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    @Override
    public String toString() {
        return "LocalFileInfo{" +
                "time=" + time +
                ", name='" + name + '\'' +
                ", path='" + path + '\'' +
                '}';
    }
}
