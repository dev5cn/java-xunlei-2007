package cn.xl;

import java.util.HashMap;
import java.util.List;
import java.util.Vector;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTable;




/**
 * 此类保存了运行时需要用到的类的实例. 所有的方法都被声明为静态的.
 * 
 */

public class SaveRunTime
{	
	
	private SaveRunTime(){}
	//表.
	private static JTable DOWN_TABLE;
	private static JTable DOWN_INFOTABLE;
	//已下载完成表.
	private static JTable TASKEND_TABLE;
	
	//顶层容器.
	private static JFrame XL;
	
	//弹出对话框 .
	private static JDialog NEWTASK_JDIALOG;
	private static NewTaskDialog NEWTASK;
	
	//所有的下载线程锁,
	private static List<Object> LIST; 
	//所有的计时线程锁.
	private static List<Object> TIMELIST;
	
	//用于计算速度.
	private static List<Object> SPEED_LIST;
	
	
	
	//暂停按钮.
	private static JButton PAUSE;
	
	private static JLabel SPEED_JLABEL;
	
	
	//已经下载的文件信息.
	private static Vector<Vector> VCC;
	
	//滚动面板.
	private static JSplitPane JSP;
	
	
	//滚动面板的上边的两个组件.
	//正在下载
	private static JScrollPane JSC_ING;
	//已下载.
	private static JScrollPane JSC_ED;
	
	
	//用于保存下载文件的信息.
	private static List<HashMap<String,String>> SAVE_HASHMAP;
	
	//从xml文件中读取的Vector.
	private static Vector<Vector> XML_VC;

	
	
	
	
	
	
	
	
	
	public static JTable getDOWN_INFOTABLE()
	{
		return DOWN_INFOTABLE;
	}

	public static void setDOWN_INFOTABLE(JTable down_infotable)
	{
		DOWN_INFOTABLE = down_infotable;
	}

	

	public static JTable getDOWN_TABLE()
	{
		return DOWN_TABLE;
	}

	public static void setDOWN_TABLE(JTable down_table)
	{
		DOWN_TABLE = down_table;
	}

	public static JScrollPane getJSC_ED()
	{
		return JSC_ED;
	}

	public static void setJSC_ED(JScrollPane jsc_ed)
	{
		JSC_ED = jsc_ed;
	}

	public static JScrollPane getJSC_ING()
	{
		return JSC_ING;
	}

	public static void setJSC_ING(JScrollPane jsc_ing)
	{
		JSC_ING = jsc_ing;
	}

	public static JSplitPane getJSP()
	{
		return JSP;
	}

	public static void setJSP(JSplitPane jsp)
	{
		JSP = jsp;
	}

	public static List<Object> getLIST()
	{
		return LIST;
	}

	public static void setLIST(List<Object> list)
	{
		LIST = list;
	}

	public static NewTaskDialog getNEWTASK()
	{
		return NEWTASK;
	}

	public static void setNEWTASK(NewTaskDialog newtask)
	{
		NEWTASK = newtask;
	}

	public static JDialog getNEWTASK_JDIALOG()
	{
		return NEWTASK_JDIALOG;
	}

	public static void setNEWTASK_JDIALOG(JDialog newtask_jdialog)
	{
		NEWTASK_JDIALOG = newtask_jdialog;
	}

	public static JButton getPAUSE()
	{
		return PAUSE;
	}

	public static void setPAUSE(JButton pause)
	{
		PAUSE = pause;
	}

	public static List<HashMap<String, String>> getSAVE_HASHMAP()
	{
		return SAVE_HASHMAP;
	}

	public static void setSAVE_HASHMAP(List<HashMap<String, String>> save_hashmap)
	{
		SAVE_HASHMAP = save_hashmap;
	}


	public static JLabel getSPEED_JLABEL()
	{
		return SPEED_JLABEL;
	}

	public static void setSPEED_JLABEL(JLabel speed_jlabel)
	{
		SPEED_JLABEL = speed_jlabel;
	}

	public static JTable getTASKEND_TABLE()
	{
		return TASKEND_TABLE;
	}

	public static void setTASKEND_TABLE(JTable taskend_table)
	{
		TASKEND_TABLE = taskend_table;
	}

	public static List<Object> getTIMELIST()
	{
		return TIMELIST;
	}

	public static void setTIMELIST(List<Object> timelist)
	{
		TIMELIST = timelist;
	}

	public static Vector<Vector> getVCC()
	{
		return VCC;
	}

	public static void setVCC(Vector<Vector> vcc)
	{
		VCC = vcc;
	}

	public static JFrame getXL()
	{
		return XL;
	}

	public static void setXL(JFrame xl)
	{
		XL = xl;
	}

	public static Vector<Vector> getXML_VC()
	{
		return XML_VC;
	}

	public static void setXML_VC(Vector<Vector> xml_vc)
	{
		XML_VC = xml_vc;
	}

	public static List<Object> getSPEED_LIST()
	{
		return SPEED_LIST;
	}

	public static void setSPEED_LIST(List<Object> speed_list)
	{
		SPEED_LIST = speed_list;
	} 


}
	
	
	
	
	
	
	