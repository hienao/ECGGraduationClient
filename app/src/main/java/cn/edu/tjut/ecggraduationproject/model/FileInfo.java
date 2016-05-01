/**
 * 
 */
package cn.edu.tjut.ecggraduationproject.model;

/**
 * @author Administrator
 *单次采集文件的信息
 */
public class FileInfo {
	
	private long fileid,userid,time;
	private String filecontents;
	public long getFileid() {
		return fileid;
	}
	public void setFileid(long fileid) {
		this.fileid = fileid;
	}
	public long getUserid() {
		return userid;
	}
	public void setUserid(long userid) {
		this.userid = userid;
	}
	public long getTime() {
		return time;
	}
	public void setTime(long time) {
		this.time = time;
	}
	public String getFilecontents() {
		return filecontents;
	}
	public void setFilecontents(String filecontents) {
		this.filecontents = filecontents;
	}
	/**带fileid的构造方法
	 * @param fileid
	 * @param userid
	 * @param time
	 * @param filecontents
	 */
	public FileInfo(long fileid, long userid, long time, String filecontents) {
		super();
		this.fileid = fileid;
		this.userid = userid;
		this.time = time;
		this.filecontents = filecontents;
	}
	/**不带fileid的构造方法
	 * @param userid
	 * @param time
	 * @param filecontents
	 */
	public FileInfo(long userid, long time, String filecontents) {
		super();
		this.fileid = 0;
		this.userid = userid;
		this.time = time;
		this.filecontents = filecontents;
	}
	/**无参构造方法
	 *
	 */
	public FileInfo() {
		super();
		this.fileid = 0;
		this.userid = 0;
		this.time = 0;
		this.filecontents = "NO DATA";
	}
	
	
}
