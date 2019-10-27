package cn.xl;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.SwingConstants;


/**
 * 文字及图片显示.
 */

public class XJLable
{
	private JLabel layoutIcon= new JLabel();
	private JLabel logoTable= new JLabel();
	private JLabel search= new JLabel();
	private JLabel newWork= new JLabel();
	private JLabel taskLable= new JLabel();
	private JLabel speedLable= new JLabel();
	private JLabel logoTable1 = new JLabel();
	private JLabel newWork1 = new JLabel();
	private JPanel bottomJPane = new JPanel();
	private JSeparator jSeparator1 = new JSeparator();
	 
	/**
	 * 固定文字,图片显示.
	 * @param jp
	 * @return
	 */
	public JPanel xJLable(JPanel jp)
	{
		
		jSeparator1.setOrientation(SwingConstants.VERTICAL);
		jSeparator1.setBounds(370, 60, 10, 46);
		jp.add(jSeparator1);

		
		//底部panel位置.
		bottomJPane.setLayout(null);
		bottomJPane.setBackground(new java.awt.Color(226, 238, 249));
		bottomJPane.add(logoTable1);
		bottomJPane.setBounds(0, 440, 770, 30);

		logoTable1.setText("\u5e03\u5c40:");
		logoTable1.setBounds(20, 10, 30, 15);
		
		//迅雷Logo部分.
		logoTable.setIcon(new ImageIcon(getClass().getResource("/image/logo.png")));
		logoTable.setBounds(0, 0, 777, 60);
		jp.add(logoTable);

		//搜索条部分.
		search.setIcon(new ImageIcon(getClass().getResource("/image/search.png")));
		search.setBounds(420, 60, 340, 50);
		jp.add(search);
		
		
		//速度显示部分.
		speedLable.setText("\u901f\u5ea6:0.00KB/S");
		
		speedLable.setBounds(660, 10, 100, 15);
		SaveRunTime.setSPEED_JLABEL(speedLable);//保存对象
		bottomJPane.add(speedLable);
		
		
		//任务管理.
		taskLable.setText("  \u4efb\u52a1\u7ba1\u7406");
		taskLable.setBounds(0, 110, 100, 20);
		jp.add(taskLable);
		
		
		//底部
		layoutIcon.setIcon(new ImageIcon(getClass().getResource("/image/layout.png")));
		bottomJPane.add(layoutIcon);
		layoutIcon.setBounds(50, 10, 20, 15);
		newWork1.setIcon(new ImageIcon(getClass().getResource("/image/network.png")));
		bottomJPane.add(newWork1);
		newWork1.setBounds(120, 10, 40, 14);
		newWork.setText("\u7f51\u7edc:");
		bottomJPane.add(newWork);
		newWork.setBounds(90, 10, 30, 15);

		return bottomJPane;
	}
}
