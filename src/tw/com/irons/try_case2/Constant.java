package tw.com.irons.try_case2;

import java.util.Calendar;

public class Constant {
	final static int DIALOG_SET_SEARCH_RANGE = 1;// �O���������ڹ�����Ԓ���K
	final static int DIALOG_SET_DATETIME = 2;// �O�����ڕr�g��Ԓ���K
	final static int DIALOG_SCH_DEL_CONFIRM = 3;// �г̄h���_�J
	final static int DIALOG_CHECK = 4;// �鿴�г�
	final static int DIALOG_ALL_DEL_CONFIRM = 5;// �h��ȫ���^���г�
	final static int DIALOG_ABOUT = 6;// �P춌�Ԓ���K

	final static int MENU_HELP = 1;// ���ܱ��h��
	final static int MENU_ABOUT = 2;// �ˆ��P�

	public static enum WhoCall {// �Д��l�{����dialogSetRange���ԛQ���Ă������ԓgone����visible
		SETTING_ALARM, // ��ʾ�O���[� ���o
		SETTING_DATE, // ��ʾ�O�����ڰ��o
		SETTING_RANGE, // ��ʾ�O���г̲��ҹ������o
		NEW, // ��ʾ�½��г̰��o
		EDIT, // ��ʾ�޸��г̰��o
		SEARCH_RESULT// ��ʾ���Ұ��o
	}

	public static enum Layout {
		WELCOME_VIEW, MAIN, // ������
		SETTING, // �г��O��
		TYPE_MANAGER, // ��͹���
		SEARCH, // ����
		SEARCH_RESULT, // ���ҽY������
		HELP, // �h������
		ABOUT
	}

	public static String getNowDateString()// �@�î�ǰ���ڷ����K�D�Q��ʽYYYY/MM/DD
	{
		Calendar c = Calendar.getInstance();
		String nowDate = Schedule.toDateString(c.get(Calendar.YEAR),
				c.get(Calendar.MONTH) + 1, c.get(Calendar.DAY_OF_MONTH));
		return nowDate;

	}

	public static String getNowTimeString()// �@�î�ǰ�r�g���K�D�Q�ɸ�ʽHH:MM
	{
		Calendar c = Calendar.getInstance();
		int nowh = c.get(Calendar.HOUR_OF_DAY);
		int nowm = c.get(Calendar.MINUTE);
		String nowTime = (nowh < 10 ? "0" + nowh : "" + nowh) + ":"
				+ (nowm < 10 ? "0" + nowm : "" + nowm);
		return nowTime;
	}
}
