package de.parmol.parsers;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.ParseException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Stack;

import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.input.SAXBuilder;

import de.parmol.Settings;
import de.parmol.util.*;
import de.parmol.FFSM.*;
import de.parmol.graph.*;


/**
 * 此类完成PDG图的解析。从PDG图转化为EPDG图，添加共享数据依赖边（SDDE），为可能存在的多边添加虚拟结点转化为单边图，
 * 将有向的EPDG图转化为无向图，同时为所有的边添加标签
 *
 *
 *
 */
public class PDGParser implements GraphParser {

	/*输入图集中PDG图的个数*/
	public static int inputGraphCount;

	/* 公共PDG图解析器对象 */
	public final static PDGParser instance = new PDGParser();

	/* 保存输入时从PDG图转化为EPDG之后的EPDG图集 */
	public final static LinkedList<DirectedListGraph> epdgGraphs = new LinkedList<DirectedListGraph>();

	/* 保存方法中对应的标签对应关系，以（方法名，（结点索引，语句描述）） 即，图和节点，节点标签和语句*/
	private static HashMap<String, HashMap<Integer, String>> labelMap = new HashMap<String, HashMap<Integer, String>>();

	public static HashMap<String, String> LabelStatementMap = new HashMap<String, String>();

	private static HashMap<String, Set<String>> LabelStatementSet = new HashMap<String, Set<String>>();
	//private static HashMap<String, String> LabelNameMap = new HashMap<String, String>();


	/* 各种边的依赖关系对应的标签 */

	// 挖掘依赖边标签
	private final static char CTRL_EDGE = 'c';
	// 数据依赖边标签
	private final static char DATA_EDGE = 'd';
	// 共享数据依赖边标签
	private final static char SHARED_EDGE = 's';
	private final static char ACT_EDGE = 'x';
	private final static char AST_EDGE = 'j';


	/* 添加的虚拟结点的标签 */
	private static int VIRTUAL_NODE = Integer.MAX_VALUE;

	/* 保存PDG信息的XML文件中的结点 */
	private final static String METHOD_NAME = "Name";

	private final static String CLASS_NAME = "Name";

	private final static String NODES = "nodes";

	private final static String NODE = "Statement";

	private final static String NODE_NAME = "no";

	private final static String STATEMENT = "statement";

	private final static String NODE_LABEL = "nodelabel";

	private final static String CTRL_DEPENDENCE = "control_dependence";

	private final static String ACTION_RELATION = "action_relation";

	private final static String AST_RELATION = "ast_relation";

	private final static String DATA_DEPENDENCE = "data_dependence";

	private final static String DEPENDEE = "dependee";

	private final static String DEPENDER = "depender";

	private final static String STMT_NO = "no";

	public boolean directed() {
		return false;
	}

	/*
	 * 在该解析器中调用creatGraph()创建的是无向图，而graph包中的无向图只是继承了Graph接口而已，所以在强制转换之后，
	 * 相当于构造了其他类型的图
	 * (non-Javadoc)
	 * @see de.parmol.parsers.GraphParser#getDesiredGraphFactoryProperties()
	 */
	public int getDesiredGraphFactoryProperties() {
		return GraphFactory.UNDIRECTED_GRAPH;
	}

	public String getNodeLabel(int nodeLabel) {
		throw new UnsupportedOperationException("不支持的操作！");
	}

	/**
	 * 返回一个类中一个方法所代表的图
	 * @param method 类中某个方法对应的Element
	 * @param factory 创建某种类型的图，由setting指定
	 * @param className 方法所在类的名称
	 * @return 方法对应的EPDG，是有向链表图
	 * @throws JDOMException
	 */
	public DirectedListGraph parse(Element method, String className, GraphFactory factory) throws JDOMException {
		String methodName = className + "." + method.getAttributeValue(METHOD_NAME);
		DirectedListGraph g = (DirectedListGraph) factory.createGraph(methodName);

		//索引->标签
		HashMap<String, Integer> nodeMap = new HashMap<String, Integer>();
		//索引->语句
		HashMap<Integer, String> statementMap = new HashMap<Integer, String>();
		Set<String> statementSet = new HashSet<String>();

		Element method_node = method.getChild(NODES);
		List<?> nodesLst = method_node.getChildren(NODE);
		int index = 0;
		for (Iterator<?> nodeIter = nodesLst.iterator(); nodeIter.hasNext();) {
			Element node = (Element) nodeIter.next();
			String nodeName = node.getAttributeValue(NODE_NAME);
			String nodeLabel = node.getAttributeValue(NODE_LABEL);
			int int_nodeLabel = Integer.parseInt(nodeLabel);

			// 添加新结点
			index = g.addNode(int_nodeLabel);
			// 构造结点名称与顶点索引的映射关系
			nodeMap.put(nodeName, new Integer(index));
			// 构造结点索引与语句描述之间的映射关系
			statementMap.put(new Integer(index), node.getAttributeValue(STATEMENT));

			if (LabelStatementSet.containsKey(nodeLabel)) {
				LabelStatementSet.get(nodeLabel).add(node.getAttributeValue(STATEMENT));
			} else {
				LabelStatementSet.put(nodeLabel, new HashSet());
				LabelStatementSet.get(nodeLabel).add(node.getAttributeValue(STATEMENT));
			}
			//获得全局的顶点标签和语句的对应关系表
			LabelStatementMap.put(nodeLabel, node.getAttributeValue(STATEMENT));
			//LabelNameMap.put(nodeLabel, nodeName);
		}
		labelMap.put(methodName, statementMap);

		Element control_node = method.getChild(CTRL_DEPENDENCE);
		// 解析控制依赖边
		if (control_node != null) {
			List<?> controlLst = control_node.getChildren(DEPENDEE);
			for (Iterator<?> ctrlIter = controlLst.iterator(); ctrlIter.hasNext();) {
				Element dependee = (Element) ctrlIter.next();
				String nodeA_name = dependee.getAttributeValue(STMT_NO);
				int nodeA = ((Integer) nodeMap.get(nodeA_name)).intValue();
				List<?> dependerLst = dependee.getChildren(DEPENDER);
				for (Iterator<?> dIter = dependerLst.iterator(); dIter.hasNext();) {
					Element depender = (Element) dIter.next();
					String nodeB_name = depender.getAttributeValue(STMT_NO);
					int nodeB = ((Integer) nodeMap.get(nodeB_name)).intValue();

					//大边：大写字母  小边：小写字母
					if( (g.getNodeLabel(nodeA) > g.getNodeLabel(nodeB)) ){
						g.addEdge(nodeA, nodeB, 'C');
					} else{
						g.addEdge(nodeA, nodeB, CTRL_EDGE);
					}

					/*g.addEdge(nodeA, nodeB, CTRL_EDGE);*/

				}
			}
		}

		LinkedList<LinkedList<Integer>> dataDepSet = new LinkedList<LinkedList<Integer>>(); // 记录数据依赖指向结点的集合的集合
		Element data_node = method.getChild(DATA_DEPENDENCE);
		// 解析数据依赖边
		if (data_node != null) {
			List<?> dataLst = data_node.getChildren(DEPENDEE);
			for (Iterator<?> dataIter = dataLst.iterator(); dataIter.hasNext();) {
				Element dependee = (Element) dataIter.next();
				String nodeA_name = dependee.getAttributeValue(STMT_NO);
				int nodeA = ((Integer) nodeMap.get(nodeA_name)).intValue();

				//记录依赖于节点nodeA的节点标签
				LinkedList<Integer> dataDepNodes = new LinkedList<Integer>();
				List<?> dependerLst = dependee.getChildren(DEPENDER);
				for (Iterator<?> dIter = dependerLst.iterator(); dIter.hasNext();) {
					Element depender = (Element) dIter.next();
					String nodeB_name = depender.getAttributeValue(STMT_NO);
					int nodeB = ((Integer) nodeMap.get(nodeB_name)).intValue();

					// 得到数据依赖指向的结点集合
					dataDepNodes.add(new Integer(nodeB));

					// 将多边转换为单边
					if (g.getEdge(nodeA, nodeB) == Graph.NO_EDGE && g.getEdge(nodeB, nodeA) == Graph.NO_EDGE) {
						//大边：大写字母  小边：小写字母
						if( (g.getNodeLabel(nodeA) > g.getNodeLabel(nodeB)) ){
							g.addEdge(nodeA, nodeB, 'D');
						} else{
							g.addEdge(nodeA, nodeB, DATA_EDGE);
						}

						/*g.addEdge(nodeA, nodeB, DATA_EDGE);*/

					} else if (g.getEdge(nodeA, nodeB) != Graph.NO_EDGE || g.getEdge(nodeB, nodeA) != Graph.NO_EDGE) {
						int nodeC = g.addNode(VIRTUAL_NODE--);

						//大边：大写字母  小边：小写字母
						if( (g.getNodeLabel(nodeA) > g.getNodeLabel(nodeB)) ){
							g.addEdge(nodeA, nodeC, 'D');
							g.addEdge(nodeC, nodeB, 'D');
						} else{
							g.addEdge(nodeA, nodeC, DATA_EDGE);
							g.addEdge(nodeC, nodeB, DATA_EDGE);
						}

						/*g.addEdge(nodeA, nodeC, DATA_EDGE);
						g.addEdge(nodeC, nodeB, DATA_EDGE);*/

					}
				}
				dataDepSet.add(dataDepNodes);
			}
		}

		Element action_node = method.getChild(ACTION_RELATION);
		if (action_node != null) {
			List<?> actionLst = action_node.getChildren(DEPENDEE);
			for (Iterator<?> actIter = actionLst.iterator(); actIter.hasNext();) {
				Element dependee = (Element) actIter.next();
				String nodeA_name = dependee.getAttributeValue(STMT_NO);
				int nodeA = ((Integer) nodeMap.get(nodeA_name)).intValue();
				List<?> dependerLst = dependee.getChildren(DEPENDER);
				for (Iterator<?> dIter = dependerLst.iterator(); dIter.hasNext();) {
					Element depender = (Element) dIter.next();
					String nodeB_name = depender.getAttributeValue(STMT_NO);
					int nodeB = ((Integer) nodeMap.get(nodeB_name)).intValue();

					//大边：大写字母  小边：小写字母
					if( (g.getNodeLabel(nodeA) > g.getNodeLabel(nodeB)) ){
						g.addEdge(nodeA, nodeB, 'X');
					} else{
						g.addEdge(nodeA, nodeB, ACT_EDGE);
					}
				}
			}
		}

		Element ast_node = method.getChild(AST_RELATION);
		if (ast_node != null) {
			List<?> actionLst = ast_node.getChildren(DEPENDEE);
			for (Iterator<?> astIter = actionLst.iterator(); astIter.hasNext();) {
				Element dependee = (Element) astIter.next();
				String nodeA_name = dependee.getAttributeValue(STMT_NO);
				int nodeA = ((Integer) nodeMap.get(nodeA_name)).intValue();
				List<?> dependerLst = dependee.getChildren(DEPENDER);
				for (Iterator<?> dIter = dependerLst.iterator(); dIter.hasNext();) {
					Element depender = (Element) dIter.next();
					String nodeB_name = depender.getAttributeValue(STMT_NO);
					int nodeB = ((Integer) nodeMap.get(nodeB_name)).intValue();

					//大边：大写字母  小边：小写字母
					if( (g.getNodeLabel(nodeA) > g.getNodeLabel(nodeB)) ){
						g.addEdge(nodeA, nodeB, 'J');
					} else{
						g.addEdge(nodeA, nodeB, AST_EDGE);
					}
				}
			}
		}

		// 添加共享数据依赖边(SDDE)
		for (Iterator<LinkedList<Integer>> dsIter = dataDepSet.iterator(); dsIter.hasNext();) {
			LinkedList<?> dsLst = (LinkedList<?>) dsIter.next();
			if (dsLst.size() > 1) {
				Object[] ds = dsLst.toArray();
				for (int i = 0; i < ds.length - 1; i++) {
					int nodeA = ((Integer) ds[i]).intValue();
					// 获取nodeA结点的所有祖先结点
					HashSet<Integer> set1 = getNodeAncestors(g, nodeA);
					for (int j = i + 1; j < ds.length; j++) {
						int nodeB = ((Integer) ds[j]).intValue();
						// 获取nodeB结点的所有祖先结点
						HashSet<Integer> set2 = getNodeAncestors(g, nodeB);
						for (Iterator<Integer> sIter = set1.iterator(); sIter.hasNext();) {
							Object n1 = sIter.next();

							/*
							 * 如果nodeA和nodeB的所有祖先结点中有相同的元素，
							 * 则说明存在一个祖先结点可以通过控制依赖边到达nodeA和nodeB
							 */
							if (set2.contains(n1)) {
								// 将多边转换为单边
								if (g.getEdge(nodeA, nodeB) == Graph.NO_EDGE && g.getEdge(nodeB, nodeA) == Graph.NO_EDGE) {
									g.addEdge(nodeA, nodeB, SHARED_EDGE);
								} else if (g.getEdge(nodeA, nodeB) != Graph.NO_EDGE || g.getEdge(nodeB, nodeA) != Graph.NO_EDGE) {
									int nodeC = g.addNode(VIRTUAL_NODE--);
									g.addEdge(nodeA, nodeC, SHARED_EDGE);
									g.addEdge(nodeC, nodeB, SHARED_EDGE);
								}
								break;
							}
						}
					}
				}
			}
		}
		inputGraphCount++;
		return g;
	}

	/**
	 * 根据给定的图g和结点node，获取node的所有祖先结点（node自身也认为是它的祖先结点）
	 *
	 * @param graph
	 * @param node
	 * @return
	 */
	private HashSet<Integer> getNodeAncestors(Graph graph, int node) {
		HashSet<Integer> set = new HashSet<Integer>();
		Stack<Integer> searcher = new Stack<Integer>();
		DirectedListGraph g = (DirectedListGraph) graph;
		set.add(new Integer(node)); // 先将node自身添加到祖先集合中
		searcher.push(new Integer(node));
		while (!searcher.empty()) {
			int curnode = ((Integer) searcher.pop()).intValue();
			int incomingEdges[] = g.getIncomingNodeEdgeSet(curnode);
			for (int i = 0; i < incomingEdges.length; i++) {
				if (g.getEdgeLabel(incomingEdges[i]) == CTRL_EDGE || g.getEdgeLabel(incomingEdges[i]) =='C') {
					int incomingNode = g.getOtherNode(incomingEdges[i], curnode);
					// 判断当前结点是否在set中出现，只有在没有出现的时候才将当前的node入栈，防止PDG图中出现环时，无限循环
					if (!set.contains(new Integer(incomingNode)) ) {
						set.add(new Integer(incomingNode));
						searcher.push(new Integer(incomingNode));
					}
				}
			}
		}
		return set;
	}

	public Graph parse(String text, GraphFactory factory) throws ParseException {
		/* 暂时没有实现 */
		throw new UnsupportedOperationException("不支持的操作！");
	}

	//返回有向链表图集
	public Graph[] parse(InputStream in, GraphFactory factory) throws IOException, ParseException {

		inputGraphCount=0;

		SAXBuilder sax = new SAXBuilder();
		epdgGraphs.clear();
		try {
			Document pdgDoc = sax.build(in);
			Element root = pdgDoc.getRootElement(); // PDG
			List<?> classes = root.getChildren(); // Class
			Iterator<?> iter = classes.iterator();
			while (iter.hasNext()) {
				Element cls = (Element) iter.next();
				String className = cls.getAttributeValue(CLASS_NAME);
				List<?> methods = cls.getChildren(); // Method
				Iterator<?> mIter = methods.iterator();
				while (mIter.hasNext()) {
					Element method = (Element) mIter.next();
					DirectedListGraph g = parse(method, className, DirectedListGraph.Factory.instance);
					/*UndirectedListGraph graph = new UndirectedListGraph();
					graph = graph.convertToUndirectedListGraph(g);
					epdgGraphs.add(graph);*/
					epdgGraphs.add(g);
				}
			}
			return (Graph[]) epdgGraphs.toArray(new Graph[epdgGraphs.size()]);
		} catch (JDOMException e) {
			e.printStackTrace();
		}
		return null;
	}

	public String serialize(Graph g) {
		if (g == null) return "";
		StringBuffer buf = new StringBuffer();

		buf.append("--------------------------------------------------------\n");

		if (g instanceof de.parmol.FFSM.Matrix) {
			buf.append("Subgraph Index: " + g.getName() + "\n");
		} else {
			buf.append("Method Name: " + g.getName() + "\n");
		}

		HashMap<Integer, String> statementMap = new HashMap<Integer, String>();
		statementMap = labelMap.get(g.getName());

		//系列化节点
		for (int i = 0; i < g.getNodeCount(); i++) {
			//if (g.getNodeLabel(i) == VIRTUAL_NODE && g.getDegree(i)==1) return null;
			buf.append("NodeIndex: " + i);
			for (int k = 0; k < 5; k++)
				buf.append(' ');
			buf.append("NodeLabel: " + g.getNodeLabel(i));
			for (int k = 0; k < 12; k++)
				buf.append(' ');

			//此处实际上存在问题。因为某一个标签对应具有同一语法结构的所有语句。得到的statement不一定是该节点对应的语句，而只是语法结构相同
			//可以在Embedding中维护每一个节点对应的语句，但是内存消耗会比较大。
			if (statementMap != null) {
				String statement = (String) statementMap.get((Integer)i);
				if (statement == null) {
					buf.append("Statement: (virtual node)");
				} else {
					buf.append("Statement: " + statement);
				}
			} else {

				Set<String> statementSet = LabelStatementSet.get(String.valueOf(g.getNodeLabel(i)));
				if (statementSet == null) {
					buf.append("Statement: (virtual node)");
				} else {
					buf.append("Statement: ");
					int maxNum = 2;
					if (Settings.outputMoreStatement == true) maxNum = 9;
					int i3 = 1;
					for (String s: statementSet) {
						if (i3++>maxNum) break;
						buf.append("\n"+s);
					}
				}
			}

			buf.append("\n");

		}

		//系列化边
		for (int i = 0; i < g.getEdgeCount(); i++) {
			final int edge = g.getEdge(i);
			buf.append("EdgeIndex: " + i);
			for (int k = 0; k < 5; k++)
				buf.append(' ');
			buf.append("EdgeLabel: " + (char) g.getEdgeLabel(edge));
			for (int k = 0; k < 5; k++)
				buf.append(' ');

			buf.append("NodeA: " + g.getNodeA(edge));
			for (int k = 0; k < 5; k++)
				buf.append(' ');
			buf.append("NodeB: " + g.getNodeB(edge));
			for (int k = 0; k < 5; k++)
				buf.append(' ');

			if (g.getEdgeLabel(edge) == CTRL_EDGE || g.getEdgeLabel(edge) == 'C') {
				buf.append("EdgeType: Control Dep");
			} else if (g.getEdgeLabel(edge) == DATA_EDGE || g.getEdgeLabel(edge) == 'D') {
				buf.append("EdgeType: Data Dep");
			} else if (g.getEdgeLabel(edge) == SHARED_EDGE) {
				buf.append("EdgeType: Shared-data Dep");
			} else if (g.getEdgeLabel(edge) == ACT_EDGE || g.getEdgeLabel(edge) == 'X') {
				buf.append("EdgeType: Action");
			} else if (g.getEdgeLabel(edge) == AST_EDGE || g.getEdgeLabel(edge) == 'J') {
				buf.append("EdgeType: AST");
			}
			buf.append('\n');
		}

		//buf.append("***********************\n");
		return buf.toString();
	}

	public void serialize(Graph[] graphs, OutputStream out) throws IOException {
		BufferedOutputStream bout = new BufferedOutputStream(out);

		for (int i = 0; i < graphs.length; i++) {
			bout.write(serialize(graphs[i]).getBytes());
		}
		bout.flush();
	}

	/*
	 * 用于输出哈希表到某一个文件中
	 */
	public static void outputHashMap(HashMap<?,?> outHashMap, String outputFileName) throws IOException
	{
		FileOutputStream out = new FileOutputStream(new File(outputFileName));
		Iterator iter = outHashMap.entrySet().iterator();
		while (iter.hasNext()) {
			Map.Entry entry = (Map.Entry) iter.next();
			Object key = entry.getKey();
			Object val = entry.getValue();
			out.write(key.toString().getBytes());
			out.write('\t');
			out.write(val.toString().getBytes());
			out.write('\n');
		}
	}


	public static void main(String[] argvs) throws Exception {
		Graph[] output;

		//FileInputStream in = new FileInputStream(new File("HFFSM/src/test/resources/test.xml"));
		FileInputStream in = new FileInputStream(new File("/Users/yumeng/JavaProjects/FixGen/codegraph/src/test/resources/73.xml"));
		output = new PDGParser().parse(in, UndirectedListGraph.Factory.instance);

		instance.serialize(output, new FileOutputStream(new File("HFFSM/src/test/resources/data/ParserOutput.txt")) );

		outputHashMap(LabelStatementMap, "HFFSM/src/test/resources/data/LabelStatementMap.txt");
	}
}