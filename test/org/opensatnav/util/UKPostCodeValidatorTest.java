package org.opensatnav.util;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;

import org.junit.Test;

public class UKPostCodeValidatorTest {

	// some strange codes are availables in the freethepostcode DB but don't
	// seem to be valid, they will be ignored during the test
	private final String[] erroneousCodes = { "BB1 3H", "BB1 3B", "BN6 2NK", "BD23 2Q",
			"D13 0HO", "D15 8CK", "DN14 7H", "E14 3Q", "E3 3D", "F62 1CA",
			"F80 2SO", "G41 2P", "HD1 6A", "HD1 6Q", "M19 1", "M23 M40",
			"NW1 8OA", "PL3 6R", "PL4 8S", "PR2 2Y", "PR23 PR2", "RH1 1MN",
			"RM20 E1", "S62 6B", "TW17 9AV", "W3 6U", "W3 0T", "WR11 4EI",
			"WV2 3L" };

	@Test
	public void testIsPostCode() {
		// postcodes taken from http://www.freethepostcode.org/currentlist
		String[] postcodes = { "AB10 6BB", "ZE2 9SF", "YO3 7BL", "YO31 0GW",
				"WV10 7JR", "TQ11 0NH", "PE12 6EW", "OX4 2FW", "OX44 9LP",
				"NE3 1UL", "N7 0JN", "  AB10 6BB "};
		for (String code : postcodes) {
			assertTrue("code " + code + " not matched as a UK postcode",
					UKPostCodeValidator.isPostCode(code));
			assertTrue("code " + code.toLowerCase()
					+ " not matched as a UK postcode", UKPostCodeValidator
					.isPostCode(code.toLowerCase()));
			assertTrue("code " + code.toUpperCase()
					+ " not matched as a UK postcode", UKPostCodeValidator
					.isPostCode(code.toUpperCase()));
		}
		String[] notPostCodes = { " ", "", null, "49330", "AAAA100 BB44",
				"London" };
		for (String code : notPostCodes) {
			assertFalse("code " + code + " matched as a UK postcode",
					UKPostCodeValidator.isPostCode(code));
		}
	}

	@Test
	public void testIsPostCodeForCurrentFreeThePostCodesList() {
		ArrayList<String> result = new ArrayList<String>();
		try {
			URL url = new URL("http://www.freethepostcode.org/currentlist");
			InputStream is = url.openStream();
			BufferedReader reader = new BufferedReader(new InputStreamReader(is));
			String line;
			while ((line = reader.readLine()) != null) {
				String[] words = line.split(" ");
				if (words.length>3) {
					// some codes have extra spaces in the available list, they're removed here
					result.add(words[2] +" " + words[words.length-1]);
				}
			}
			reader.close();
			is.close();
			
		} catch (IOException e) {
			e.printStackTrace();
		}

		// strange format seen in the freethepostcode DB, not sure they're really valid
		for (String code : erroneousCodes)
			result.remove(code);

		
		// postcodes taken from http://www.freethepostcode.org/currentlist
		for (String code : result) {
			assertTrue("code " + code +" not matched as a UK postcode", UKPostCodeValidator.isPostCode(code));
			assertTrue("code " + code.toLowerCase() +" not matched as a UK postcode", UKPostCodeValidator.isPostCode(code.toLowerCase()));
			assertTrue("code " + code.toUpperCase() + " not matched as a UK postcode", UKPostCodeValidator.isPostCode(code.toUpperCase()));
		}
	}
}
