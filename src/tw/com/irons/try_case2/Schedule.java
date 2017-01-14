package tw.com.irons.try_case2;

import static tw.com.irons.try_case2.Constant.getNowDateString;
import static tw.com.irons.try_case2.Constant.getNowTimeString;
import static tw.com.irons.try_case2.db.DBUtil2.getSNFromPrefs;
import android.util.Log;

public class Schedule {
	private int sn;// ÿһ���г̌���һ����һ�o����sn�a�����Y�ώ��О����I
	private String date1;// �г�����
	private String time1;// �г̕r�g
	private String date2;// �[�����
	private String time2;// �[犕r�g
	private String type;// �г����
	private String title;// �г̘��}
	private String note;// �г̂��]
	private boolean timeSet;// �г��Ƿ��O�þ��w�r�g
	private boolean alarmSet;// �г��Ƿ��O���[�
	private int littleImage;

	// �������г̕r���R�r�Y�ϣ�ֻ��Ҫ�����������Y�ϣ��Á��ڄ����M���½��г̽����հ��������A�O�O�óɮ�ǰ����
	public Schedule(int y, int m, int d) {
		sn = 0;
		date1 = toDateString(y, m, d);
		time1 = toTimeString(8, 0);// �r�gĬ�J8�c

		date2 = null;
		time2 = null;

		title = "";
		note = "";
		type = "";

		timeSet = true;
		alarmSet = false;

	}

	// �˘���������Y�ώ��xȡ�г�����r��
	public Schedule(int sn, String date1, String time1, String date2,
			String time2, String title, String note, String type,
			String timeSet, String alarmSet, int littleImage) {
		this.sn = sn;
		this.date1 = date1;
		this.time1 = time1;
		this.date2 = date2;
		this.time2 = time2;
		this.title = title;
		this.note = note;
		this.type = type;
		this.timeSet = Boolean.parseBoolean(timeSet);
		this.alarmSet = Boolean.parseBoolean(alarmSet);
		this.littleImage = littleImage;
	}

	public int getYear()// �@����
	{
		String[] date = date1.split("/");
		int tmp = Integer.valueOf(date[0]);
		return tmp;
	}

	public int getMonth()// �@����
	{
		String[] date = date1.split("/");
		int tmp = Integer.valueOf(date[1]);
		return tmp;
	}

	public int getDay()// �@����
	{
		String[] date = date1.split("/");
		int tmp = Integer.valueOf(date[2]);
		return tmp;
	}

	public int getHour()// �@�Õr
	{
		String[] time = time1.split(":");
		int tmp = Integer.valueOf(time[0]);
		return tmp;
	}

	public int getMinute()// �@�÷�
	{
		String[] time = time1.split(":");
		int tmp = Integer.valueOf(time[1]);
		return tmp;
	}

	public int getAYear()// �@���[犵���
	{
		String[] date = date2.split("/");
		int tmp = Integer.valueOf(date[0]);
		return tmp;
	}

	public int getAMonth()// �@���[���
	{
		String[] date = date2.split("/");
		int tmp = Integer.valueOf(date[1]);
		return tmp;
	}

	public int getADay()// �@���[���
	{
		String[] date = date2.split("/");
		int tmp = Integer.valueOf(date[2]);
		return tmp;
	}

	public int getAHour()// �@���[犕r
	{
		String[] time = time2.split(":");
		int tmp = Integer.valueOf(time[0]);
		return tmp;
	}

	public int getAMin()// �@���[犷�
	{
		String[] time = time2.split(":");
		int tmp = Integer.valueOf(time[1]);
		return tmp;
	}

	public void setType(String s)// �O�����
	{
		this.type = s;
	}

	public String getType()// �@�����
	{
		return type;
	}

	public void setTitle(String s)// �O�Ø��}
	{
		this.title = s;
	}

	public String getTitle()// �@�Ø��}
	{
		return title;
	}

	public void setNote(String s)// �O�Â��]
	{
		this.note = s;
	}

	public String getNote()// �@�Â��]
	{
		return note;
	}

	public void setTimeSet(boolean b)// �O���Ƿ��O�þ��w�r�g�Ĳ���ֵ
	{
		this.timeSet = b;
		if (!timeSet)// �����false�f���]���O�þ��w�r�g���t���w�r�gĬ�J�鮔������һ���
		{
			time1 = "23:59";
		}
	}

	public boolean getTimeSet()// �õ��Ƿ��O�˕r�g
	{
		return timeSet;
	}

	public void setAlarmSet(boolean b)// �O���Ƿ��O���[犵Ĳ���ֵ
	{
		this.alarmSet = b;
		if (!timeSet)// �����false�f���]���O���[犣��t�[���null
		{
			date2 = null;
			time2 = null;
		}
	}

	public boolean getAlarmSet()// �õ��Ƿ��O�����[�
	{
		return alarmSet;
	}

	public void setDate1(String y, String m, String d)// �O���г����ڣ��D�Q��YYYY/MM/DD
	{
		StringBuffer sb = new StringBuffer();
		sb.append(y);
		sb.append("/");
		sb.append(m);
		sb.append("/");
		sb.append(d);
		date1 = sb.toString();
	}

	public String getDate1()// �õ��г�����
	{
		return date1;
	}

	public void setTime1(String h, String m)// �O���г̕r�g���D�Q��HH:MM
	{
		StringBuffer sb = new StringBuffer();
		sb.append(h);
		sb.append(":");
		sb.append(m);
		time1 = sb.toString();
	}

	public String getTime1()// �@���г̕r�g
	{
		return time1;
	}

	public void setDate2(String y, String m, String d)// �O���[�����
	{
		StringBuffer sb = new StringBuffer();
		sb.append(y);
		sb.append("/");
		sb.append(m);
		sb.append("/");
		sb.append(d);
		date2 = sb.toString();
	}

	public String getDate2()// �õ��[�����
	{
		return date2;
	}

	public void setTime2(String h, String m)// �O���[犕r�g
	{
		StringBuffer sb = new StringBuffer();
		sb.append(h);
		sb.append(":");
		sb.append(m);
		time2 = sb.toString();
	}

	public String getTime2()// �õ��[犕r�g
	{
		return time2;
	}

	public void setSn(int sn)// �O��sn�a
	{
		this.sn = sn;
	}

	public int getSn() // �õ�sn�a
	{
		return sn;
	}

	public static String toDateString(int y, int m, int d)// �o�B��������int�͵��������D�Q��YYYY/MM/DD
	{
		StringBuffer sb = new StringBuffer();
		sb.append(y);
		sb.append("/");
		sb.append(m < 10 ? "0" + m : "" + m);
		sb.append("/");
		sb.append(d < 10 ? "0" + d : "" + d);
		return sb.toString();
	}

	public String toTimeString(int h, int m)// ��int�͵ĕr���D�Q��HH:MM
	{
		StringBuffer sb = new StringBuffer();
		sb.append(h < 10 ? "0" + h : "" + h);
		sb.append(":");
		sb.append(m < 10 ? "0" + m : "" + m);
		return sb.toString();
	}

	public String typeForListView()// �Á�õ����������ListView�e�@ʾ����͸�ʽ
	{
		StringBuffer sbTmp = new StringBuffer();
		sbTmp.append("[");
		sbTmp.append(type);
		sbTmp.append("]");
		return sbTmp.toString();
	}

	public String dateForListView()// �Á�õ����������ListView�e�@ʾ�����ڸ�ʽ
	{
		StringBuffer sbTmp = new StringBuffer();
		sbTmp.append(date1);
		sbTmp.append("   ");
		return sbTmp.toString();
	}

	public String timeForListView()// �Á�õ����������ListView�e�@ʾ�ĕr�g��ʽ
	{
		if (!timeSet) {
			return "- -:- -   ";
		}
		StringBuffer sbTmp = new StringBuffer();
		sbTmp.append(time1);
		sbTmp.append("   ");
		return sbTmp.toString();
	}

	public boolean isPassed()// ׌�г��O�Õr�g�c��ǰ�r�g��ȣ��Д��г��Ƿ����^��
	{
		String nowDate = getNowDateString();
		String nowTime = getNowTimeString();
		String schDate = date1;
		String schTime = timeSet ? time1 : "23:59";// ����г̛]���O�Õr�g���t�J���^�ˮ���23:59��Ҳ���ǵ��˵ڶ�����^�r

		if (nowDate.compareTo(schDate) > 0
				|| (nowDate.compareTo(schDate) == 0 && nowTime
						.compareTo(schTime) > 0)) {
			return true;
		}
		return false;
	}

	public String toInsertSql(MySchedule father)// �@ȡschedule��������Y�ώ�r��sql�Z��
	{
		StringBuffer sb = new StringBuffer();
		sb.append("insert into schedule values(");
		sn = getSNFromPrefs(father);
		sb.append(sn);
		sb.append(",'");
		sb.append(date1);
		sb.append("','");
		sb.append(time1);
		sb.append("','");
		sb.append(date2);
		sb.append("','");
		sb.append(time2);
		sb.append("','");
		sb.append(title);
		sb.append("','");
		sb.append(note);
		sb.append("','");
		sb.append(type);
		sb.append("','");
		sb.append(timeSet);
		sb.append("','");
		sb.append(alarmSet);
		sb.append("','");
		sb.append(littleImage);
		sb.append("')");
		Log.d("toInsertSql", sb.toString());
		return sb.toString();
	}

	public String toUpdateSql(MySchedule father)// �@ȡschedule������r��sql�Z��
	{
		int preSn = sn;// ӛ�֮ǰ��sn
		StringBuffer sb = new StringBuffer();
		sb.append("update schedule set _id=");
		sn = getSNFromPrefs(father);// �Q���µ�sn
		sb.append(sn);
		sb.append(",date1='");
		sb.append(date1);
		sb.append("',time1='");
		sb.append(time1);
		sb.append("',date2='");
		sb.append(date2);
		sb.append("',time2='");
		sb.append(time2);
		sb.append("',title='");
		sb.append(title);
		sb.append("',note='");
		sb.append(note);
		sb.append("',type='");
		sb.append(type);
		sb.append("',timeset='");
		sb.append(timeSet);
		sb.append("',alarmset='");
		sb.append(alarmSet);
		sb.append("',littleImage='");
		sb.append(littleImage);
		sb.append("' where _id=");
		sb.append(preSn);
		Log.d("toUpdateSql", sb.toString());
		return sb.toString();
	}

	public void setLittleImage(int i)// �O�Ø��}
	{
		this.littleImage = i;
	}

	public int getLittleImage()// �O�Ø��}
	{
		return this.littleImage;
	}
}
