package cn.edu.tjut.ecggraduationproject.model;

public class YangbenWaveInfo {
	private long waveid,userid,time;
	private String data;
	public long getWaveid() {
		return waveid;
	}
	public void setWaveid(long waveid) {
		this.waveid = waveid;
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
	public String getData() {
		return data;
	}
	public void setData(String data) {
		this.data = data;
	}
	/**有waveid构造方法
	 * @param waveid
	 * @param userid
	 * @param time
	 * @param data
	 */
	public YangbenWaveInfo(long waveid, long userid, long time, String data) {
		super();
		this.waveid = waveid;
		this.userid = userid;
		this.time = time;
		this.data = data;
	}
	/**无waveid构造方法
	 * @param userid
	 * @param time
	 * @param data
	 */
	public YangbenWaveInfo(long userid, long time, String data) {
		super();
		this.waveid = 0;
		this.userid = userid;
		this.time = time;
		this.data = data;
	}
	/**无参数构造方法
	 * 
	 */
	public YangbenWaveInfo() {
		super();
		this.waveid = 0;
		this.userid = 0;
		this.time = 0;
		this.data = "无效数据";
	}
	
	
}
