
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
 * ��������,�̳���JFrame.
 * 
 */

public class xunlei extends JFrame 
{
	private JPanel jPanel1 = new JPanel();
	private static final long serialVersionUID = 2291521166551729185L;
	
	
	/**
	 * ˽�й�����.��ʼ������,�ͳ�������ʱ��Ҫ�õ��Ĳ��ּ�����.
	 *
	 */
	private xunlei()
    {
        try
        {   //�������.
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
       
        //���������߳����ļ�����,
		List<Object> list=Collections.synchronizedList(new ArrayList<Object>());
		//�����ʱ�߳����ļ�����.
		List<Object> timeList=Collections.synchronizedList(new ArrayList<Object>());
		//��ʱ�ٶȼ���.
		List<Object> speed_List=Collections.synchronizedList(new ArrayList<Object>());
		
		SaveRunTime.setLIST(list);
		SaveRunTime.setTIMELIST(timeList);
		SaveRunTime.setSPEED_LIST(speed_List);
		
		//�õ���ò�������ʱ,�������ļ���Ϣ�ı���.
		List<HashMap<String, String>> testHashMap=Collections.synchronizedList(new ArrayList<HashMap<String, String>>());
		SaveRunTime.setSAVE_HASHMAP(testHashMap);
		
		
		//������JDialog��ʼ��,����.
		NewTaskDialog newTask=new NewTaskDialog(this);
		
		//newTask Dialog�ĳ�ʼ��
		newTask.initComponents();
		//�����JDialog
		SaveRunTime.setNEWTASK(newTask);
		
		//����������ʼ��.
        initComponents();  
    }
	
	
	/**
	 * ���������ĳ�ʼ������.���е�����������ﱻ���.
	 *
	 */
    private void initComponents()
    {
        //û�в���.
        getContentPane().setLayout(null);
        //λ��
        setBounds(50, 0, 777, 523);
      
        //�رմ����¼�,�˳�����֮ǰ,дxml.
        addWindowListener(new WindowAdapter()
		{
			public void windowClosing(WindowEvent e) 
			{
				try
				{
					new XmlOperation().writerTaskInfo();//�˳�����֮ǰ,д��xml
				} catch (Exception ex)
				{
				}
				System.exit(1);
			}
		});
        
       
        //jPanel1û�в��ֹ�����.
        jPanel1.setLayout(null);
        jPanel1.setBackground(new java.awt.Color(226, 238, 249));
        jPanel1.setFocusable(false);
        jPanel1.setBounds(0, 0, 769, 440);
  
        
        
        //���ع����ָ���,�ָ��������߷ֱ����ļ����ؽ�����ʾ��,�ļ���Ϣ��
        JSplitPane jsp=this.getJSp();
        XDownTable xd= new XDownTable();
        
        JTable taskEndTable=xd.taskComplete();
        //����һ�ſյı�,��ʽ������ʹ�õı���ͬ,������ʾ�Ѿ���ɵ�����.
        SaveRunTime.setTASKEND_TABLE(taskEndTable);
        JScrollPane jsced=xd.getJScrollPane(taskEndTable);
       
        //���������ñ�.�������¹���һ��.
        JScrollPane jscing= xd.getJScrollPane(xd.xDownTable());
        SaveRunTime.setJSC_ING(jscing);//�������������ñ�;
        //�����Ѿ������ñ�.���������û�м�����������Ϣ��.
        SaveRunTime.setJSC_ED(jsced);
        SaveRunTime.setJSP(jsp);//����������.
        
        //�������ر�.
        jsp.setLeftComponent(jscing);
        //������Ϣ��.����Ҫ��������.
		jsp.setRightComponent(new XDownInfoTable().getDownInfo());
		
		//������.
		add(jsp);
        
		
		
        //����JMenuBar;
        setJMenuBar(new XJMenuBar().xJMenuBar());
        
        
        //���ذ�ť��.
        new XJButton().xJButton(jPanel1);
        
        
        //���صײ�Panel��jPanel1�ļ��Ź̶�ͼƬ
        getContentPane().add(new XJLable().xJLable(jPanel1));
        
        //���������.
        new XLeftTree().xLeftTree(jPanel1);
        
        //���ض���jPanel
        getContentPane().add(jPanel1);
        
        //����Ϊ���ܵ�����С.
        setResizable(false);
       
    }
   
  
    /**
     * ����һ��JSplitPane
     * @return
     */
    public JSplitPane getJSp()
    {
    	JSplitPane jsp=new JSplitPane();
       //�ϱ�����Ŀɼ��߶�.
		jsp.setDividerLocation(200);
		jsp.setDividerSize(6);//�������Ŀ��.����������ɫû�а취�ı�.
		jsp.setOrientation(JSplitPane.VERTICAL_SPLIT);
		jsp.setBounds(110, 114, 660, 326);
		//���±߿�.
		jsp.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0,Color.gray));
        return jsp;
    }
    
    
   
    public static void main(String args[])
    {
    		xunlei xl=new xunlei();
    		xl.setVisible(true);
    	
    		//��������.
    		SaveRunTime.setXL(xl);
    		
    		//���㼴ʱ�ٶ�.
    		new Thread(new DownLoad().new CounterSpeed()).start();
    }
}





























