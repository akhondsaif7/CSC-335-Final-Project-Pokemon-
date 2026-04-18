package controller;

import static org.junit.jupiter.api.Assertions.*;

import java.io.IOException;

import org.junit.jupiter.api.Test;

import model.*;
import mons.*;
import mons.Lapras;
import moves.Blizzard;
import moves.Covet;
import moves.Facade;
import moves.Glaciate;
import moves.HyperFang;
import moves.RazorLeaf;
import moves.*;
import mons.*;

class CombatControllerTests {

	@Test
	void testPlayerGetters() {
		Mon[] p1Mons = {new Venusaur()};
		Player p1 = new Player(p1Mons);
		Mon[] p2Mons = {new Lapras()};
		Player p2 = new Player(p2Mons);
		CombatController cc = new CombatController(p1, p2);
		assertEquals(cc.getP1(), p1);
		assertEquals(cc.getP2(), p2);
	}
	
	@Test
	void testGetPlayerMoves() {
		Mon[] p1Mons = {new Venusaur()};
		Player p1 = new Player(p1Mons);
		Mon[] p2Mons = {new Lapras()};
		Player p2 = new Player(p2Mons);
		CombatController cc = new CombatController(p1, p2);
		Move[] moves = {new VineWhip(), new HyperFang(), new Covet(), new RazorLeaf()};
		for (int i = 0; i < 4; i++) {
			assertEquals(moves[i].getClass(), cc.getPlayerMoves(1)[i].getClass());
		}
		Move[] moves2 = {new Blizzard(), new Glaciate(), new Covet(), new Facade()};
		for (int i = 0; i < 4; i++) {
			assertEquals(moves2[i].getClass(), cc.getPlayerMoves(2)[i].getClass());
		}
	}
	
	@Test
	void testIncorretGetPlayerMoves() {
		Mon[] p1Mons = {new Venusaur()};
		Player p1 = new Player(p1Mons);
		Mon[] p2Mons = {new Lapras()};
		Player p2 = new Player(p2Mons);
		CombatController cc = new CombatController(p1, p2);
		assertThrows(IllegalArgumentException.class, () -> {
			cc.getPlayerMoves(0);
		});
	}
	
	@Test
	void testSwitchMon() throws ClassNotFoundException, IOException {
		Mon[] p1Mons = {new Venusaur(), new Lapras()};
		Player p1 = new Player(p1Mons);
		Mon[] p2Mons = {new Lapras(), new Venusaur()};
		Player p2 = new Player(p2Mons);
		CombatController cc = new CombatController(p1, p2);
		cc.switchMon(1, 1);
		assertEquals(cc.getP1().getActiveMon().getClass(), Lapras.class);
		cc.switchMon(2,1);
		assertEquals(cc.getP2().getActiveMon().getClass(), Bulbasaur.class);
		cc.switchMon(1, 1);
		assertEquals(cc.getP1().getActiveMon().getClass(), Lapras.class);
	}
	
	@Test
	void testInvalidSwitchMon() {
		Mon[] p1Mons = {new Bulbasaur(), new Lapras()};
		Player p1 = new Player(p1Mons);
		Mon[] p2Mons = {new Lapras(), new Bulbasaur()};
		Player p2 = new Player(p2Mons);
		CombatController cc = new CombatController(p1, p2);
		assertThrows(IllegalArgumentException.class, () -> {
			cc.switchMon(1, 3);
		});
	}
	
	@Test
	void testMakeMove() throws ClassNotFoundException, IOException {
		Mon[] p1Mons = {new Bulbasaur(), new Lapras()};
		Player p1 = new Player(p1Mons);
		Mon[] p2Mons = {new Lapras(), new Bulbasaur()};
		Player p2 = new Player(p2Mons);
		CombatController cc = new CombatController(p1, p2);
		int oldPP = cc.getPlayerMoves(1)[0].getPP();
		cc.makeMove(1, 0);
		assertTrue(oldPP != cc.getPlayerMoves(1)[0].getPP());
		assertTrue(cc.getP2().getActiveMon().getMaxHp() != cc.getP2().getActiveMon().getCurrHp());
	}
	
	@Test
	void testMakeMove2() throws ClassNotFoundException, IOException {
		Mon[] p1Mons = {new Bulbasaur(), new Lapras()};
		Player p1 = new Player(p1Mons);
		Mon[] p2Mons = {new Lapras(), new Bulbasaur()};
		Player p2 = new Player(p2Mons);
		CombatController cc = new CombatController(p1, p2);
		int oldPP = cc.getPlayerMoves(2)[0].getPP();
		cc.makeMove(2, 0);
		assertTrue(oldPP != cc.getPlayerMoves(2)[0].getPP());
		assertTrue(cc.getP1().getActiveMon().getMaxHp() != cc.getP1().getActiveMon().getCurrHp());
	}
	
	@Test
	void testFainted() throws ClassNotFoundException, IOException {
		Mon[] p1Mons = {new Bulbasaur()};
		Player p1 = new Player(p1Mons);
		Mon[] p2Mons = {new Lapras()};
		Player p2 = new Player(p2Mons);
		CombatController cc = new CombatController(p1, p2);
		while(cc.getP1().getActiveMon().getCurrHp() > 0) {
			cc.makeMove(2, 0);
		}
		assertTrue(cc.getP1().getActiveMon().hasFainted());
	}
	
	@Test
	void testLivingMonCount() throws ClassNotFoundException, IOException {
		Mon[] p1Mons = {new Bulbasaur()};
		Player p1 = new Player(p1Mons);
		Mon[] p2Mons = {new Lapras()};
		Player p2 = new Player(p2Mons);
		CombatController cc = new CombatController(p1, p2);
		assertEquals(cc.getP1().livingMonCount(), 1);
		assertFalse(cc.getP1().hasLost());
		while(cc.getP1().getActiveMon().getCurrHp() > 0) {
			cc.makeMove(2, 0);
		}
		assertEquals(cc.getP1().livingMonCount(), 0);
		assertTrue(cc.getP1().hasLost());
	}
	
	@Test
	void testFaintedSwitch() throws ClassNotFoundException, IOException {
		Mon[] p1Mons = {new Bulbasaur(), new Lapras()};
		Player p1 = new Player(p1Mons);
		Mon[] p2Mons = {new Lapras(), new Bulbasaur()};
		Player p2 = new Player(p2Mons);
		CombatController cc = new CombatController(p1, p2);
		while(cc.getP1().getActiveMon().getCurrHp() > 0) {
			cc.makeMove(2, 0);
		}
		assertThrows(IllegalArgumentException.class, () -> {
			cc.switchMon(1, 0);
		});
	}
	
	@Test
	void testIncorrectTeamSize() {
		assertThrows(IllegalArgumentException.class, () -> {
			Player p = new Player(new Mon[0]);
		});
		assertThrows(IllegalArgumentException.class, () -> {
			Mon[] mons = {new Bulbasaur(), new Lapras(), new Bulbasaur(), new Lapras(), new Bulbasaur(), new Lapras(),
					new Bulbasaur(), new Lapras(), new Bulbasaur(), new Lapras()};
			Player p = new Player(mons);
		});
	}
	
	@Test
	void testMakeComputerMove() throws ClassNotFoundException, IOException {
		Mon[] p1Mons = {new Bulbasaur(), new Lapras()};
		Player p1 = new Player(p1Mons);
		Mon[] p2Mons = {new Lapras(), new Bulbasaur()};
		Player p2 = new Player(p2Mons);
		CombatController cc = new CombatController(p1, p2);
		cc.makeComputerMove();
		assertTrue(p1.getActiveMon().getCurrHp() != p1.getActiveMon().getMaxHp());
	}
	
	@Test
	void testComputerSwitch() throws ClassNotFoundException, IOException {
		Mon[] p1Mons = {new Bulbasaur(), new Lapras()};
		Player p1 = new Player(p1Mons);
		Mon[] p2Mons = {new Lapras(), new Bulbasaur()};
		Player p2 = new Player(p2Mons);
		CombatController cc = new CombatController(p1, p2);
		while(!cc.getP2().getActiveMon().hasFainted()) {
			cc.makeMove(1, 0);
		}
		cc.makeComputerMove();
		assertEquals(p2.getActiveMon().getName(), "Bulbasaur");
		assertTrue(p1.getActiveMon().getCurrHp() != p1.getActiveMon().getMaxHp());
	}
	
	@Test
	void testHardVsEasyAI() throws ClassNotFoundException, IOException {
		Mon[] p1MonsHard = {new Bulbasaur(), new Lapras()};
		Player p1Hard = new Player(p1MonsHard);
		Mon[] p2MonsHard = {new Lapras(), new Bulbasaur()};
		Player p2Hard = new Player(p2MonsHard);
		CombatController ccHard = new CombatController(p1Hard, p2Hard, CombatController.Difficulty.hard);
		Mon[] p1MonsEasy = {new Bulbasaur(), new Lapras()};
		Player p1Easy = new Player(p1MonsEasy);
		Mon[] p2MonsEasy = {new Lapras(), new Bulbasaur()};
		Player p2Easy = new Player(p2MonsEasy);
		CombatController ccEasy = new CombatController(p1Easy, p1Easy, CombatController.Difficulty.easy);
		ccHard.makeComputerMove();
		ccEasy.makeComputerMove();
		assertFalse(ccHard.getP1().getActiveMon().getCurrHp() == ccEasy.getP1().getActiveMon().getCurrHp());
	}

}