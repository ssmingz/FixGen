package de.parmol;

import processing.core.*;
import java.util.*;

import de.parmol.graph.Graph;
import de.parmol.parsers.PDGParser;

/**
 * 此类完成图的图形可视化输出
 * 利用Processing包实现。
 *
 * 
 */
public class GraphView extends PApplet{
	int nodeCount;
	ArrayList<Node> nodes = new ArrayList();
	HashMap<Integer,Node> nodeTable = new HashMap<Integer, Node>();//索引和节点
	
	int edgeCount;
	ArrayList<Edge> edges = new ArrayList();
	
	Node selection;
	
	//需要输出的图形
	Graph g;
	//用于控制PApplet的循环执行
	Timer loopTimer = new Timer();
	
	
	static final int nodeColor = 0xFFF0C070;
	static final int selectColor = 0xFFFF3030;
	static final int fixedColor = 0xFFFF8080;
	static final int edgeColor = 0xFF000000;
	
	GraphView(Graph g){
		this.g = g;
	}
	
	/*
	 * 可视化输出节点类
	 */
	class Node {
		float x, y;
		float dx, dy;
		boolean fixed;
		String label;
		int index;
		
		/**
		 * 
		 * @param index 节点在矩阵中的索引
		 * @param label 节点的标签
		 * @param width 当前图面板的宽度 确定适当的点坐标
		 * @param height 当前图面板的高度 确定适当的点坐标
		 */
		Node(int index, String label) {
			this.index = index;
			this.label = label;
			x = new Random().nextInt(width);
			y = new Random().nextInt(height);
		}
		
		//调整节点在绘制面板上的位置
		void relax() {
			float ddx = 0;
			float ddy = 0;
			
			for (int j=0; j < nodeCount; j++) {
				Node n = nodes.get(j);
				if (n != this) {
					float vx = x -n.x;
					float vy = y -n.y;
					float lensq = vx*vx+vy*vy;
					if (lensq == 0) {
						ddx += new Random().nextInt(1);
						ddy += new Random().nextInt(1);
					} else if (lensq < 100*100) {
						ddx += vx/lensq;
						ddy += vy/lensq;
					}
				}
			}
			
			float dlen = mag(ddx, ddy) /2;
			if (dlen>0) {
				dx += ddx / dlen;
				dy += ddy / dlen;
			}
			
		}//end of void relax()
		
		//更新节点在绘制面板上的位置
		void update() {
			if (!fixed) {
				x += constrain(dx, -5, 5);
				y += constrain(dy, -5, 5);
				
				x = constrain(x, 0, width);
				y = constrain(y, 0, height);
			}
			
			dx /=2;
			dy /=2;
		}
		
		//绘制节点和标签
		void draw() {
			if (selection == this) {
				fill(selectColor);
			} else if (fixed) {
				fill(fixedColor);
			} else {
				fill(nodeColor);
			}
			
			stroke(0);
			strokeWeight(0.5f);
			
			ellipseMode(CENTER);
			float r = textWidth(label)+10;
			ellipse(x, y, r, r);
			
			fill(0);
			textAlign(CENTER, CENTER);
			text(label, x, y);
			
		}
		
	}//end of class Node
	
	
	/*
	 * 可视化输出边类
	 */
	class Edge {
		Node from;
		Node to;
		float len;
		String label;
		
		Edge(Node from, Node to, String label) {
			this.from = from;
			this.to = to;
			this.label =label;
			this.len = width/5;
		}
		
		void relax(){
			float vx = to.x-from.x;
			float vy = to.y-from.y;
			float d = mag(vx, vy);
			this.len = width/5;
			if (d>0) {
				float f =(len-d)/(d*3);
				float dx=f*vx;
				float dy =f*vy;
				to.dx += dx;
				to.dy += dy;
				from.dx -= dx;
				from.dy -= dy;
			}
		}//end of void relax()
		
		/*
		 * 绘制边、标签和边的方向（箭头）
		 */
		void draw() {
			stroke(edgeColor);
			strokeWeight(0.35f);
			line(from.x, from.y, to.x, to.y);
			
			fill(0);
			textAlign(CENTER, CENTER);
			text(this.label.toLowerCase(), (from.x+to.x)/2, (from.y+to.y)/2-20);
			
			//绘制边的方向箭头
			Node end;
			//是小边，则边方向从标签小的节点到大的节点
			if (this.label.compareTo("Z") > 0){
				end= (Integer.parseInt(from.label)-Integer.parseInt(to.label))<0?to:from;
			} else {
				end = (Integer.parseInt(from.label)-Integer.parseInt(to.label))>0?to:from;
			}
			
			float mx=(from.x+to.x)/2;
			float my=(from.y+to.y)/2;
			
			stroke(edgeColor);
			strokeWeight(0.35f);
			
			if (mx<end.x && my<end.y){
				line(mx, my, mx-20, my);
				line(mx, my, mx, my-20);
			}else if (mx>end.x && my<end.y){	
				line(mx, my, mx+20, my);
				line(mx, my, mx, my-20);
			} else if (mx>end.x && my>end.y) {
				line(mx, my, mx+20, my);
				line(mx, my, mx, my+20);
			} else if (mx<end.x && my>end.y) {
				line(mx, my, mx-20, my);
				line(mx, my, mx, my+20);
			}

			
		}//end of void draw()
		
	}//end of class Edge
	
	public void setup() {
		//size(600, 600);
		loadData();
		//noLoop();
	}
	
	public void loadData() {
		//添加节点
		for (int i = 0; i < g.getNodeCount(); i++) {
			Node n = new Node(i, String.valueOf(g.getNodeLabel(i)) );
			nodes.add(n);
			nodeTable.put(i, n);
		}
		
		//添加边
		for (int i = 0; i < g.getEdgeCount(); i++) {
			final int edge = g.getEdge(i);

			Node from = nodeTable.get( g.getNodeA(edge));
			Node to = nodeTable.get( g.getNodeB(edge));

			Edge e = new Edge(from, to, (char)g.getEdgeLabel(edge)+"");
			edges.add(e);
		}
		
	}//end of loadData
	
	public void draw() {
		background(255);
		
		
		for (Edge e : edges) {
			e.relax();
		}
		for (Node n : nodes) {
			n.relax();
		}
		for(Node n: nodes) {
			n.update();
		}
		
		for(Edge e: edges) {
			e.draw();
		}
		
		for(Node n: nodes) {
			n.draw();
		}
		
		if (selection != null) {
			String s = PDGParser.LabelStatementMap.get(selection.label);
			if (s==null) s = "virtual node";
			fill(0);
			textAlign(CENTER, CENTER);
			text(s, selection.x + textWidth(selection.label) + 0.6f*textWidth(s), selection.y);
		}
		/*if (g.getNodeCount() > 10) {
			loopTimer.schedule(new SetNoLoop(), 15000);
		}*/
	}
	
	
	public void mousePressed(){
		float closest = 20;
		for (Node n:nodes){
			float d = dist(mouseX, mouseY, n.x, n.y);
			if (d<closest){
				selection =n;
				closest=d;
			}
		}
		
		if (selection != null) {
			if(mouseButton == LEFT) {
				selection.fixed = true;
			} else if(mouseButton == RIGHT) {
				selection.fixed = false;
			}
		}
	}
	
	public void mouseDragged(){
		if (selection != null) {
			selection.x = mouseX;
			selection.y = mouseY;
		}
	}
	
	public void mouseRealeased(){
		selection = null;
	}
	
	
	class SetNoLoop extends TimerTask{
		public void run(){
			noLoop();
		}
	}
	
	class SetLoop extends TimerTask{
		public void run(){
			loop();
		}
	}
	
	
}//end of class GraphView
