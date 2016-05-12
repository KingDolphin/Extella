package com.leonmontealegre.utils;

/**
 * The Key enum. This enum has all of the keys that LibGDX supports.
 * 
 * @author Leon Montealegre
 * @version 0.03
 */
public enum Key {
	UNKNOWN(0),
	SOFT_LEFT(1),
	META_SHIFT_ON(1),
	SOFT_RIGHT(2),
	META_ALT_ON(2),
	HOME(3),
	BACK(4),
	META_SYM_ON(4),
	CALL(5),
	ENDCALL(6),
	NUM_0(7,'0'),
	NUM_1(8,'1'),
	NUM_2(9,'2'),
	NUM_3(10,'3'),
	NUM_4(11,'4'),
	NUM_5(12,'5'),
	NUM_6(13,'6'),
	NUM_7(14,'7'),
	NUM_8(15,'8'),
	NUM_9(16,'9'),
	META_ALT_LEFT_ON(16),
	STAR(17,'*'),
	POUND(18,'#'),
	UP(19),
	DPAD_UP(19),
	DOWN(20),
	DPAD_DOWN(20),
	LEFT(21),
	DPAD_LEFT(21),
	RIGHT(22),
	DPAD_RIGHT(22),
	DPAD_CENTER(23),
	CENTER(23),
	VOLUME_UP(24),
	VOLUME_DOWN(25),
	POWER(26,'^'),
	CAMERA(27),
	CLEAR(28),
	A(29),
	B(30),
	C(31),
	D(32),
	META_ALT_RIGHT_ON(32),
	E(33),
	F(34),
	G(35),
	H(36),
	I(37),
	J(38),
	K(39),
	L(40),
	M(41),
	N(42),
	O(43),
	P(44),
	Q(45),
	R(46),
	S(47),
	T(48),
	U(49),
	V(50),
	W(51),
	X(52),
	Y(53),
	Z(54),
	COMMA(55,','),
	PERIOD(56,'.'),
	LEFT_ALT(57),
	RIGHT_ALT(58),
	LEFT_SHIFT(59),
	RIGHT_SHIFT(60),
	TAB(61,'\t'),
	SPACE(62,' '),
	SYM(63),
	EXPLORER(64),
	META_SHIFT_LEFT_ON(64),
	ENVELOPE(65),
	ENTER(66,'\n'),
	BACKSPACE(67),
	GRAVE(68,'`'),
	MINUS(69,'-'),
	EQUALS(70,'='),
	LEFT_BRACKET(71,'['),
	RIGHT_BRACKET(72,']'),
	BACKSLASH(73,'\\'),
	SEMICOLON(74,';'),
	APOSTROPHE(75,'\''),
	SLASH(76,'/'),
	AT(77,'@'),
	NUM(78),
	HEADSETHOOK(79),
	FOCUS(80),
	PLUS(81,'+'),
	MENU(82),
	NOTIFICATION(83),
	SEARCH(84),
	MEDIA_PLAY_PAUSE(85),
	MEDIA_STOP(86),
	MEDIA_NEXT(87),
	MEDIA_PREVIOUS(88),
	MEDIA_REWIND(89),
	MEDIA_FAST_FORWARD(90),
	MUTE(91),
	PAGE_UP(92),
	PAGE_DOWN(93),
	PICTSYMBOLS(94),
	SWITCH_CHARSET(95),
	BUTTON_A(96),
	BUTTON_B(97),
	BUTTON_C(98),
	BUTTON_X(99),
	BUTTON_Y(100),
	BUTTON_Z(101),
	BUTTON_L1(102),
	BUTTON_R1(103),
	BUTTON_L2(104),
	BUTTON_R2(105),
	BUTTON_THUMBL(106),
	BUTTON_THUMBR(107),
	BUTTON_START(108),
	BUTTON_SELECT(109),
	BUTTON_MODE(110),
	DEL(112),
	META_SHIFT_RIGHT_ON(128),
	CONTROL_LEFT(129),
	CONTROL_RIGHT(130),
	ESCAPE(131),
	END(132),
	INSERT(133),
	NUMPAD_0(144,'0'),
	NUMPAD_1(145,'1'),
	NUMPAD_2(146,'2'),
	NUMPAD_3(147,'3'),
	NUMPAD_4(148,'4'),
	NUMPAD_5(149,'5'),
	NUMPAD_6(150,'6'),
	NUMPAD_7(151,'7'),
	NUMPAD_8(152,'8'),
	NUMPAD_9(153,'9'),
	COLON(243,':'),
	F1(244),
	F2(245),
	F3(246),
	F4(247),
	F5(248),
	F6(249),
	F7(250),
	F8(251),
	F9(252),
	F10(253),
	F11(254),
	F12(255),
	BUTTON_CIRCLE(255);
		
	/** The numerical value of the key. */
	public final int keyValue;
	
	/** Name of the key on a typical keyboard. */
	private final char name;
	
	/**
	 * Creates a new key.
	 * 
	 * @param	keyValue
	 * 			The value of the key.
	 */
	private Key(int keyValue) {
		this.keyValue = keyValue;
		this.name = (this.name().length() == 1 ? this.name().charAt(0) : '\0');
	}
	
	/**
	 * Creates a new key with a specified name.
	 * 
	 * @param	keyValue
	 * 			The value of the key.
	 * 
	 * @param	name
	 * 			The physical name of the key for translation to text.
	 */
	private Key(int keyValue, char name) {
		this.keyValue = keyValue;
		this.name = name;
	}
	
	/**
	 * Gets the key with the given key value.
	 * 
	 * @param	num
	 * 			The key value of the key.
	 * 
	 * @return	The key with the corresponding {@code keyValue}
	 */
	public static Key getKey(int num) {
		for (Key key : Key.values()) {
			if (key.keyValue == num)
				return key;
		}
		return null;
	}
	
	@Override
	public String toString() {
		return ""+this.name;
	}
	
}