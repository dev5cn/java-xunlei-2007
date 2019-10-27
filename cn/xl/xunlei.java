
package cn.xl;
import java.awt.Color;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.UIManager;


/**
 * 顶层容器,继承自JFrame.
 * 
 */

public class xunlei extends JFrame 
{
	private JPanel jPanel1 = new JPanel();
	private static final long serialVersionUID = 2291521166551729185L;
	
	
	/**
	 * 私有构造子.初始化容器,和程序运行时需要用到的部分集合类.
	 *
	 */
	private xunlei()
    {
        try
        {   //本地外观.
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
       
        //保存下载线程锁的集合类,
		List<Object> list=Collections.synchronizedList(new ArrayList<Object>());
		//保存计时线程锁的集合类.
		List<Object> timeList=Collections.synchronizedList(new ArrayList<Object>());
		//即时速度计算.
		List<Object> speed_List=Collections.synchronizedList(new ArrayList<Object>());
		
		SaveRunTime.setLIST(list);
		SaveRunTime.setTIMELIST(timeList);
		SaveRunTime.setSPEED_LIST(speed_List);
		
		//得到或得不到链接时,对下载文件信息的保存.
		List<HashMap<String, String>> testHashMap=Collections.synchronizedList(new ArrayList<HashMap<String, String>>());
		SaveRunTime.setSAVE_HASHMAP(testHashMap);
		
		
		//新任务JDialog初始化,隐藏.
		NewTaskDialog newTask=new NewTaskDialog(this);
		
		//newTask Dialog的初始化
		newTask.initComponents();
		//保存此JDialog
		SaveRunTime.setNEWTASK(newTask);
		
		//顶层容器初始化.
        initComponents();  
    }
	
	
	/**
	 * 顶层容器的初始化方法.所有的组件都在这里被添加.
	 *
	 */
    private void initComponents()
    {
        //没有布局.
        getContentPane().setLayout(null);
        //位置
        setBounds(50, 0, 777, 523);
      
        //关闭窗口事件,退出程序之前,写xml.
        addWindowListener(new WindowAdapter()
		{
			public void windowClosing(WindowEvent e) 
			{
				try
				{
					new XmlOperation().writerTaskInfo();//退出程序之前,写入xml
				} catch (Exception ex)
				{
				}
				System.exit(1);
			}
		});
        
       
        //jPanel1没有布局管理器.
        jPanel1.setLayout(null);
        jPanel1.setBackground(new java.awt.Color(226, 238, 249));
        jPanel1.setFocusable(false);
        jPanel1.setBounds(0, 0, 769, 440);
  
        
        
        //加载滚动分隔符,分隔符的两边分别是文件下载进度显示表,文件信息表
        JSplitPane jsp=this.getJSp();
        XDownTable xd= new XDownTable();
        
        JTable taskEndTable=xd.taskComplete();
        //构造一张空的表,样式与正在使用的表相同,用于显示已经完成的任务.
        SaveRunTime.setTASKEND_TABLE(taskEndTable);
        JScrollPane jsced=xd.getJScrollPane(taskEndTable);
       
        //上面下载用表.必须重新构造一个.
        JScrollPane jscing= xd.getJScrollPane(xd.xDownTable());
        SaveRunTime.setJSC_ING(jscing);//保存正在下载用表;
        //保存已经下载用表.程序最初并没有加载已下载信息表.
        SaveRunTime.setJSC_ED(jsced);
        SaveRunTime.setJSP(jsp);//存滚动条组件.
        
        //上面下载表.
        jsp.setLeftComponent(jscing);
        //下面信息表.不需要构造两张.
		jsp.setRightComponent(new XDownInfoTable().getDownInfo());
		
		//添加组件.
		add(jsp);
        
		
		
        //加载JMenuBar;
        setJMenuBar(new XJMenuBar().xJMenuBar());
        
        
        //加载按钮区.
        new XJButton().xJButton(jPanel1);
        
        
        //加载底部Panel及jPanel1的几张固定图片
        getContentPane().add(new XJLable().xJLable(jPanel1));
        
        //加载左边树.
        new XLeftTree().xLeftTree(jPanel1);
        
        //加载顶部jPanel
        getContentPane().add(jPanel1);
        
        //设置为不能调整大小.
        setResizable(false);
       
    }
   
  
    /**
     * 返回一个JSplitPane
     * @return
     */
    public JSplitPane getJSp()
    {
    	JSplitPane jsp=new JSplitPane();
       //上边组件的可见高度.
		jsp.setDividerLocation(200);
		jsp.setDividerSize(6);//滚动条的宽度.滚动条的颜色没有办法改变.
		jsp.setOrientation(JSplitPane.VERTICAL_SPLIT);
		jsp.setBounds(110, 114, 660, 326);
		//有下边框.
		jsp.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0,Color.gray));
        return jsp;
    }
    
    
   
    public static void main(String args[])
    {
    		xunlei xl=new xunlei();
    		xl.setVisible(true);
    	
    		//顶层容器.
    		SaveRunTime.setXL(xl);
    		
    		//计算即时速度.
    		new Thread(new DownLoad().new CounterSpeed()).start();
    }
}





























