package bgWork.handler;

import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.event.MouseEvent;
import java.util.Vector;

import javax.swing.JPanel;
import javax.swing.border.LineBorder;

import Define.AreaDefine;
import Listener.CPHActionListener;
import Pack.DragPack;
import Pack.SendText;
import bgWork.InitProcess;
import mod.ILinePainter;
import mod.instance.AssociationLine;
import mod.instance.BasicClass;
import mod.instance.CompositionLine;
import mod.instance.DependencyLine;
import mod.instance.GeneralizationLine;
import mod.instance.GroupContainer;
import mod.instance.UseCase;

public class CanvasPanelHandler extends PanelHandler {
	Vector<JPanel> members = new Vector<>();
	Vector<JPanel> lines = new Vector<>();
	Vector<JPanel> selectComp = new Vector<>();
	int boundShift = 10;

	public CanvasPanelHandler(JPanel Container, InitProcess process) {
		super(Container, process);
		boundDistance = 10;
		initContextPanel();
		Container.add(this.contextPanel);
	}

	@Override
	void initContextPanel() {
		JPanel fphContextPanel = core.getFuncPanelHandler().getContectPanel();
		contextPanel = new JPanel();
		contextPanel.setBounds(
				fphContextPanel.getLocation().x
						+ fphContextPanel.getSize().width + boundShift,
				fphContextPanel.getLocation().y, 800, 600);
		contextPanel.setLayout(null);
		contextPanel.setVisible(true);
		contextPanel.setBackground(Color.WHITE);
		contextPanel.setBorder(new LineBorder(Color.BLACK));
		contextPanel.addMouseListener(new CPHActionListener(this));
	}

	@Override
	public void ActionPerformed(MouseEvent e) {
		switch (core.getCurrentFuncIndex()) {
			case 0:
				selectByClick(e);
				break;
			case 1:
			case 2:
			case 3:
				addLine(core.getCurrentFunc(), new DragPack(e.getPoint(), e.getPoint()));
				break;
			case 4:
			case 5:
				addObject(core.getCurrentFunc(), e.getPoint());
				break;

			default:
				break;
		}
		repaintComp();
	}

	public void ActionPerformed(DragPack dp) {
		switch (core.getCurrentFuncIndex()) {
			case 0:
				selectByDrag(dp);
				break;
			case 1:
			case 2:
			case 3:
				addLine(core.getCurrentFunc(), dp);
				break;
			case 4:
			case 5:
				break;
			case 6: // Dependency
				addLine(core.getCurrentFunc(), dp);
			default:
				break;
		}
		repaintComp();
	}

	public void repaintComp() {
		// 先重畫所有線段
		for (JPanel line : lines) {
			line.repaint();
		}
		// 再重畫所有元件
		for (JPanel comp : members) {
			comp.repaint();
		}
		// 整個面板也 repaint 一下
		contextPanel.repaint();
	}

	void selectByClick(MouseEvent e) {
		// 1. 清除所有线的高亮
		for (JPanel line : lines) {
			if (line instanceof ILinePainter) {
				((ILinePainter) line).setHighlight(false);
			}
		}

		boolean isSelect = false;
		selectComp = new Vector<>();

		// 2. 遍历每个组件，看鼠标是否点到它的 port
		for (int i = 0; i < members.size(); i++) {
			JPanel comp = members.elementAt(i);
			if (isInside(comp, e.getPoint()) && !isSelect) {
				// 3. 计算在哪个 port
				Point clickAbs = e.getPoint();
				int side = new AreaDefine().getArea(comp.getLocation(), comp.getSize(), clickAbs);

				// 4. 只有点到有效 port（side != OUT_SIDE）时，才做选取和高亮
				if (side != new AreaDefine().OUT_SIDE) {
					// a) 标记被选中组件
					switch (core.isFuncComponent(comp)) {
						case 0:
							((BasicClass) comp).setSelect(true);
							break;
						case 1:
							((UseCase) comp).setSelect(true);
							break;
						case 6:
							((GroupContainer) comp).setSelect(true);
							break;
						default:
							break;
					}
					selectComp.add(comp);
					isSelect = true;

					// b) 高亮所有连到此 port 的线
					// b) 高亮所有连到此 port 的线
					for (JPanel line : lines) {
						if (line instanceof AssociationLine) {
							AssociationLine al = (AssociationLine) line;
							if ((al.getFromComponent() == comp && al.getFromSide() == side)
									|| (al.getToComponent() == comp && al.getToSide() == side)) {
								al.setHighlight(true);
							}
						} else if (line instanceof CompositionLine) {
							CompositionLine cl = (CompositionLine) line;
							if ((cl.getFromComponent() == comp && cl.getFromSide() == side)
									|| (cl.getToComponent() == comp && cl.getToSide() == side)) {
								cl.setHighlight(true);
							}
						} else if (line instanceof GeneralizationLine) {
							GeneralizationLine gl = (GeneralizationLine) line;
							if ((gl.getFromComponent() == comp && gl.getFromSide() == side)
									|| (gl.getToComponent() == comp && gl.getToSide() == side)) {
								gl.setHighlight(true);
							}
						} else if (line instanceof DependencyLine) {
							DependencyLine dl = (DependencyLine) line;
							if ((dl.getFromComponent() == comp && dl.getFromSide() == side)
									|| (dl.getToComponent() == comp && dl.getToSide() == side)) {
								dl.setHighlight(true);
							}
						}
					}

				}
			} else {
				// 5. 没有选中组件时，取消它的 select
				setSelectAllType(comp, false);
			}
		}

		// 6. 重绘所有组件和线
		repaintComp();
	}

	boolean groupIsSelect(GroupContainer container, Point point) {
		for (int i = 0; i < container.getComponentCount(); i++) {
			if (core.isGroupContainer(container.getComponent(i))) {
				point.x -= container.getComponent(i).getLocation().x;
				point.y -= container.getComponent(i).getLocation().y;
				if (groupIsSelect((GroupContainer) container.getComponent(i),
						point) == true) {
					return true;
				} else {
					point.x += container.getComponent(i).getLocation().x;
					point.y += container.getComponent(i).getLocation().y;
				}
			} else if (core.isJPanel(container.getComponent(i))) {
				if (isInside((JPanel) container.getComponent(i), point)) {
					return true;
				}
			}
		}
		return false;
	}

	boolean selectByDrag(DragPack dp) {
		if (isInSelect(dp.getFrom()) == true) {
			// dragging components
			Dimension shift = new Dimension(dp.getTo().x - dp.getFrom().x,
					dp.getTo().y - dp.getFrom().y);
			for (int i = 0; i < selectComp.size(); i++) {
				JPanel jp = selectComp.elementAt(i);
				jp.setLocation(jp.getLocation().x + shift.width,
						jp.getLocation().y + shift.height);
				if (jp.getLocation().x < 0) {
					jp.setLocation(0, jp.getLocation().y);
				}
				if (jp.getLocation().y < 0) {
					jp.setLocation(jp.getLocation().x, 0);
				}
			}
			return true;
		}
		if (dp.getFrom().x > dp.getTo().x && dp.getFrom().y > dp.getTo().y) {
			// drag right down from to left up
			groupInversSelect(dp);
			return true;
		} else if (dp.getFrom().x < dp.getTo().x && dp.getFrom().y < dp.getTo().y) {
			// drag from left up to right down
			groupSelect(dp);
			return true;
		}
		return false;
	}

	public void setGroup() {
		if (selectComp.size() > 1) {
			GroupContainer gContainer = new GroupContainer(core);
			gContainer.setVisible(true);
			Point p1 = new Point(selectComp.elementAt(0).getLocation().x,
					selectComp.elementAt(0).getLocation().y);
			Point p2 = new Point(selectComp.elementAt(0).getLocation().x,
					selectComp.elementAt(0).getLocation().y);
			Point testP;
			for (int i = 0; i < selectComp.size(); i++) {
				testP = selectComp.elementAt(i).getLocation();
				if (p1.x > testP.x) {
					p1.x = testP.x;
				}
				if (p1.y > testP.y) {
					p1.y = testP.y;
				}
				if (p2.x < testP.x + selectComp.elementAt(i).getSize().width) {
					p2.x = testP.x + selectComp.elementAt(i).getSize().width;
				}
				if (p2.y < testP.y + selectComp.elementAt(i).getSize().height) {
					p2.y = testP.y + selectComp.elementAt(i).getSize().height;
				}
			}
			p1.x--;
			p1.y--;
			gContainer.setLocation(p1);
			gContainer.setSize(p2.x - p1.x + 2, p2.y - p1.y + 2);
			for (int i = 0; i < selectComp.size(); i++) {
				JPanel temp = selectComp.elementAt(i);
				removeComponent(temp);
				gContainer.add(temp, i);
				temp.setLocation(temp.getLocation().x - p1.x,
						temp.getLocation().y - p1.y);
			}
			addComponent(gContainer);
			selectComp = new Vector<>();
			selectComp.add(gContainer);
			repaintComp();
		}
	}

	public void setUngroup() {
		int size = selectComp.size();
		for (int i = 0; i < size; i++) {
			if (core.isGroupContainer(selectComp.elementAt(i))) {
				GroupContainer gContainer = (GroupContainer) selectComp
						.elementAt(i);
				Component temp;
				int j = 0;
				while (gContainer.getComponentCount() > 0) {
					temp = gContainer.getComponent(0);
					temp.setLocation(
							temp.getLocation().x + gContainer.getLocation().x,
							temp.getLocation().y + gContainer.getLocation().y);
					addComponent((JPanel) temp, j);
					selectComp.add((JPanel) temp);
					gContainer.remove(temp);
					j++;
				}
				removeComponent(gContainer);
				selectComp.remove(gContainer);
			}
			repaintComp();
		}
	}

	void groupSelect(DragPack dp) {
		JPanel jp = new JPanel();
		jp.setLocation(dp.getFrom());
		jp.setSize(Math.abs(dp.getTo().x - dp.getFrom().x),
				Math.abs(dp.getTo().y - dp.getFrom().x));
		selectComp = new Vector<>();
		for (int i = 0; i < members.size(); i++) {
			if (isInside(jp, members.elementAt(i)) == true) {
				selectComp.add(members.elementAt(i));
				setSelectAllType(members.elementAt(i), true);
			} else {
				setSelectAllType(members.elementAt(i), false);
			}
		}
	}

	void groupInversSelect(DragPack dp) {
		JPanel jp = new JPanel();
		jp.setLocation(dp.getTo());
		jp.setSize(Math.abs(dp.getTo().x - dp.getFrom().x),
				Math.abs(dp.getTo().y - dp.getFrom().x));
		selectComp = new Vector<>();
		for (int i = 0; i < members.size(); i++) {
			if (isInside(jp, members.elementAt(i)) == false) {
				selectComp.add(members.elementAt(i));
				setSelectAllType(members.elementAt(i), true);
			} else {
				setSelectAllType(members.elementAt(i), false);
			}
		}
	}

	boolean isInSelect(Point point) {
		for (int i = 0; i < selectComp.size(); i++) {
			if (isInside(selectComp.elementAt(i), point) == true) {
				return true;
			}
		}
		return false;
	}

	void addLine(JPanel funcObj, DragPack dPack) {
		// 找出連接的元件物件
		for (JPanel member : members) {
			if (isInside(member, dPack.getFrom())) {
				dPack.setFromObj(member);
			}
			if (isInside(member, dPack.getTo())) {
				dPack.setToObj(member);
			}
		}
		if (dPack.getFromObj() == dPack.getToObj() || dPack.getFromObj() == contextPanel
				|| dPack.getToObj() == contextPanel) {
			return;
		}

		// 不動 members，線段加入 lines
		if (!lines.contains(funcObj)) {
			lines.add(funcObj);
		}

		switch (core.isLine(funcObj)) {
			case 0:
				((AssociationLine) funcObj).setConnect(dPack);
				break;
			case 1:
				((CompositionLine) funcObj).setConnect(dPack);
				break;
			case 2:
				((GeneralizationLine) funcObj).setConnect(dPack);
				break;
			case 3:
				((DependencyLine) funcObj).setConnect(dPack);
				break;
			default:
				break;
		}
		contextPanel.add(funcObj, 0);
		funcObj.setVisible(true);
		funcObj.repaint();
	}

	void addObject(JPanel funcObj, Point point) {
		if (members.size() > 0) {
			members.insertElementAt(funcObj, 0);
		} else {
			members.add(funcObj);
		}
		members.elementAt(0).setLocation(point);
		members.elementAt(0).setVisible(true);
		contextPanel.add(members.elementAt(0), 0);
	}

	public boolean isInside(JPanel container, Point point) {
		Point cLocat = container.getLocation();
		Dimension cSize = container.getSize();
		if (point.x >= cLocat.x && point.y >= cLocat.y) {
			if (point.x <= cLocat.x + cSize.width
					&& point.y <= cLocat.y + cSize.height) {
				return true;
			}
		}
		return false;
	}

	public boolean isInside(JPanel container, JPanel test) {
		Point cLocat = container.getLocation();
		Dimension cSize = container.getSize();
		Point tLocat = test.getLocation();
		Dimension tSize = test.getSize();
		if (cLocat.x <= tLocat.x && cLocat.y <= tLocat.y) {
			if (cLocat.x + cSize.width >= tLocat.x + tSize.width
					&& cLocat.y + cSize.height >= tLocat.y + tSize.height) {
				return true;
			}
		}
		return false;
	}

	public JPanel getSingleSelectJP() {
		if (selectComp.size() == 1) {
			return selectComp.elementAt(0);
		}
		return null;
	}

	public void setContext(SendText tr) {
		System.out.println(tr.getText());
		try {
			switch (core.isClass(tr.getDest())) {
				case 0:
					((BasicClass) tr.getDest()).setText(tr.getText());
					break;
				case 1:
					((UseCase) tr.getDest()).setText(tr.getText());
					break;
				default:
					break;
			}
		} catch (Exception e) {
			System.err.println("CPH error");
		}
	}

	void addComponent(JPanel comp) {
		contextPanel.add(comp, 0);
		members.insertElementAt(comp, 0);
	}

	void addComponent(JPanel comp, int index) {
		contextPanel.add(comp, index);
		members.insertElementAt(comp, index);
	}

	public void removeComponent(JPanel comp) {
		contextPanel.remove(comp);
		members.remove(comp);
		lines.remove(comp);
	}

	void setSelectAllType(Object obj, boolean isSelect) {
		switch (core.isFuncComponent(obj)) {
			case 0:
				((BasicClass) obj).setSelect(isSelect);
				break;
			case 1:
				((UseCase) obj).setSelect(isSelect);
				break;
			case 2:
				((AssociationLine) obj).setSelect(isSelect);
				break;
			case 3:
				((CompositionLine) obj).setSelect(isSelect);
				break;
			case 4:
				((GeneralizationLine) obj).setSelect(isSelect);
				break;
			case 5: // <- 新增
				((DependencyLine) obj).setSelect(isSelect);
				break;
			case 6:
				((GroupContainer) obj).setSelect(isSelect);
				break;
			default:
				break;
		}
	}

	public Point getAbsLocation(Container panel) {
		Point location = panel.getLocation();
		while (panel.getParent() != contextPanel) {
			panel = panel.getParent();
			location.x += panel.getLocation().x;
			location.y += panel.getLocation().y;
		}
		return location;
	}
}
