package com.leonmontealegre.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.XmlReader;

import java.io.IOException;

public final class Options {

	public static final String TITLE;

	public static int TARGET_UPS;

	public static final String astronautsRemainingText;
	public static final String winText;
	public static final String tutorialText0;
	public static final String levelCompleteText;

	static {
		XmlReader reader = new XmlReader();
		XmlReader.Element settRoot = null;
		XmlReader.Element langRoot = null;
		try {
			settRoot = reader.parse(Gdx.files.internal("XML/settings.xml"));
			langRoot = reader.parse(Gdx.files.internal("XML/lang/strings-en.xml"));
		} catch (IOException e) {
			e.printStackTrace();
		}
		TITLE = settRoot.getChild(0).get("title");
		TARGET_UPS = settRoot.getChild(1).getInt("target_ups");

		astronautsRemainingText = langRoot.getChildByName("astronauts_remaining_text").getText().replace("\\n", "\n");
		winText = langRoot.getChildByName("win_text").getText().replace("\\n", "\n");
		tutorialText0 = langRoot.getChildByName("tutorial_text_0").getText().replace("\\n", "\n");
		levelCompleteText = langRoot.getChildByName("level_complete_text").getText().replace("\\n", "\n");
	}
	
	private Options() {}
	
}
