package cn.edu.tjut.ecggraduationproject.model;

public class UserInfo {
	private long userid,phone;
	private int age;
	private String name,photopath,sex,pwdString;

	
	public long getUserid() {
		return userid;
	}

	public void setUserid(long userid) {
		this.userid = userid;
	}

	public int getAge() {
		return age;
	}

	public void setAge(int age) {
		this.age = age;
	}

	public long getPhone() {
		return phone;
	}

	public void setPhone(long phone) {
		this.phone = phone;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getPhotopath() {
		return photopath;
	}

	public void setPhotopath(String photopath) {
		this.photopath = photopath;
	}

	public String getSex() {
		return sex;
	}

	public void setSex(String sex) {
		this.sex = sex;
	}

	public String getPwdString() {
		return pwdString;
	}

	public void setPwdString(String pwdString) {
		this.pwdString = pwdString;
	}

	/**
	 * 无userid构造方法
	 * @param age
	 * @param phone
	 * @param name
	 * @param photopath
	 * @param sex
	 * @param pwdString
	 */
	public UserInfo(int age, long phone, String name, String photopath,
			String sex, String pwdString) {
		super();
		this.userid=-1;
		this.age = age;
		this.phone = phone;
		this.name = name;
		this.photopath = photopath;
		this.sex = sex;
		this.pwdString = pwdString;
	}

	/**有Userid构造方法
	 * @param userid
	 * @param age
	 * @param phone
	 * @param name
	 * @param photopath
	 * @param sex
	 * @param pwdString
	 */
	public UserInfo(long userid, int age, long phone, String name,
			String photopath, String sex, String pwdString) {
		super();
		this.userid = userid;
		this.age = age;
		this.phone = phone;
		this.name = name;
		this.photopath = photopath;
		this.sex = sex;
		this.pwdString = pwdString;
	}
	/**无参构造方法
	 *
	 */
	public UserInfo() {
		super();
		this.userid = -1;
		this.age = 0;
		this.phone = 0;
		this.name = "NO NAME";
		this.photopath = "";
		this.sex = "FAULT";
		this.pwdString = "NO";
	}

	@Override
	public String toString() {
		return "UserInfo [userid=" + userid + ", age=" + age + ", phone="
				+ phone + ", name=" + name + ", photopath=" + photopath
				+ ", sex=" + sex + ", pwdString=" + pwdString + "]";
	}
	
	
}