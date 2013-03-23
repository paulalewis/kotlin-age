package com.castlefrog.agl.domains.hex.test;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.castlefrog.agl.TurnType;
import com.castlefrog.agl.domains.hex.HexSimulator;
import com.castlefrog.agl.domains.hex.HexState;

public class TestHexSimulator {
	
	HexSimulator simulator_;

	@Before
	public void setUp() throws Exception {
		simulator_ = new HexSimulator(4, TurnType.SEQUENTIAL);
	}

	@After
	public void tearDown() throws Exception {
		simulator_ = null;
	}

	@Test
	public void test() {
		List<HexState> testStates = new ArrayList<HexState>();
		testStates.add(new HexState(new byte[][]{{0,1,1,0},{2,1,2,2},{1,2,1,1},{1,2,2,2}}, 0));
		testStates.add(new HexState(new byte[][]{{0,1,1,0},{2,1,2,2},{1,2,1,1},{1,2,2,2}}, 1));
		testStates.add(new HexState(new byte[][]{{0,1,0,1},{2,1,0,0},{1,2,2,2},{0,0,0,1}}, 0));
		testStates.add(new HexState(new byte[][]{{0,1,0,1},{2,1,0,0},{1,2,2,2},{0,0,0,1}}, 1));
		testStates.add(new HexState(new byte[][]{{0,0,0,1},{1,0,0,1},{2,2,2,2},{1,0,0,0}}, 0));
		testStates.add(new HexState(new byte[][]{{0,0,0,1},{1,0,0,1},{2,2,2,2},{1,0,0,0}}, 1));
		for (HexState testState: testStates) {
			System.out.println(testState.toString());
			simulator_.setState(testState);
			System.out.println(simulator_.getReward(testState.getAgentTurn()));
			assertTrue(simulator_.getLegalActions(testState.getAgentTurn()).isEmpty());
		}
	}

}
