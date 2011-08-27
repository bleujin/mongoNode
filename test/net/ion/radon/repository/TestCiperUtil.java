package net.ion.radon.repository;

import java.util.Date;

import junit.framework.TestCase;
import net.ion.framework.util.Debug;
import net.ion.framework.util.HashFunction;
import net.ion.framework.util.HashUtil;
import net.ion.radon.repository.util.CipherUtil;

import org.apache.commons.codec.binary.Hex;

public class TestCiperUtil extends TestCase{

	public void testEncrypt() throws Exception {
		String pwd = "redf1234redf1234redf1234redf1234redf1234";
		byte[] encrypted = CipherUtil.recursiveEncrypt(pwd) ;
		
		Debug.debug(encrypted) ;
		assertEquals(true, CipherUtil.isMatch(pwd, encrypted)) ;
		
		Debug.debug(Hex.encodeHex(encrypted)) ;
	}
	
	public void testRecursiveEncrypt() throws Exception {
		final String password = "bleujin";
		byte[] encrypt = CipherUtil.recursiveEncrypt(password) ;
		
		assertTrue(CipherUtil.isMatch(password, encrypt)) ;
		assertFalse(CipherUtil.isMatch("other", encrypt)) ;
	}

	

}
