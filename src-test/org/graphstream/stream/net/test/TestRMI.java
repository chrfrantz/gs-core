package org.graphstream.stream.net.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.util.LinkedList;

import org.graphstream.graph.Edge;
import org.graphstream.graph.Graph;
import org.graphstream.graph.Node;
import org.graphstream.graph.implementations.DefaultGraph;
import org.graphstream.stream.rmi.RMISink;
import org.graphstream.stream.rmi.RMISource;
import org.junit.Test;

public class TestRMI {

	@Test
	public void test() {
		RMISink sink;
		RMISource source;

		Graph g1 = new DefaultGraph("g1");
		Graph g2 = new DefaultGraph("g2");

		try {
			LocateRegistry.createRegistry(1099);
		} catch (Exception e) {

		}

		try {
			String name = "__test_rmi_source";

			sink = new RMISink();
			g1.addSink(sink);

			source = new RMISource();
			source.addSink(g2);

			source.bind(name);
			sink.register("//localhost/" + name);
		} catch (RemoteException e) {
			fail();
		}

		Node A = g1.addNode("A");
		Node B = g1.addNode("B");
		Node C = g1.addNode("C");

		Edge AB = g1.addEdge("AB", "A", "B", false);
		Edge AC = g1.addEdge("AC", "A", "C", true);
		Edge BC = g1.addEdge("BC", "B", "C", false);

		A.addAttribute("int", 1);
		B.addAttribute("string", "test");
		C.addAttribute("double", 2.0);

		AB.addAttribute("points",
				(Object) (new double[][] { { 1, 1 }, { 2, 2 } }));
		LinkedList<Integer> list = new LinkedList<Integer>();
		list.add(1);
		list.add(2);
		AC.addAttribute("list", list);
		BC.addAttribute("boolean", true);

		// -----

		A = g2.getNode("A");
		B = g2.getNode("B");
		C = g2.getNode("C");

		assertNotNull(A);
		assertNotNull(B);
		assertNotNull(C);
		assertEquals(g2.getNodeCount(), 3);

		AB = g2.getEdge("AB");
		AC = g2.getEdge("AC");
		BC = g2.getEdge("BC");

		assertNotNull(AB);
		assertNotNull(AC);
		assertNotNull(BC);
		assertEquals(g2.getEdgeCount(), 3);

		assertEquals("A", AB.getNode0().getId());
		assertEquals("B", AB.getNode1().getId());
		assertEquals("A", AC.getNode0().getId());
		assertEquals("C", AC.getNode1().getId());
		assertEquals("B", BC.getNode0().getId());
		assertEquals("C", BC.getNode1().getId());
		
		assertTrue(!AB.isDirected());
		assertTrue(AC.isDirected());
		assertTrue(!BC.isDirected());
		
		assertEquals(A.getAttribute("int"), 1);
		assertEquals(B.getAttribute("string"), "test");
		assertEquals(C.getAttribute("double"), 2.0);

		try {
			double[][] points = AB.getAttribute("points");

			assertEquals(points.length, 2);
			assertEquals(points[0].length, 2);
			assertEquals(points[1].length, 2);
			assertEquals(points[0][0], 1.0);
			assertEquals(points[0][1], 1.0);
			assertEquals(points[1][0], 2.0);
			assertEquals(points[1][1], 2.0);
		} catch (ClassCastException e) {
			fail();
		} catch (NullPointerException e) {
			fail();
		}

		assertEquals(list, AC.getAttribute("list"));
		assertTrue((Boolean) BC.getAttribute("boolean"));
	}
}
