package com.abc.driver.utility;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public final class PasswordService
{
  private static PasswordService instance;
  private PasswordService()
  {
  }
  public synchronized String encrypt(String plaintext) throws CellSiteDefException
  {
    MessageDigest md = null;
    try
    {
      md = MessageDigest.getInstance("SHA"); //step 2
    }
    catch(NoSuchAlgorithmException e)
    {
      throw new CellSiteDefException(e.getMessage());
    }
    try
    {
      md.update(plaintext.getBytes("UTF-8")); //step 3
    }
    catch(UnsupportedEncodingException e)
    {
      throw new CellSiteDefException(e.getMessage());
    }
    byte raw[] = md.digest(); //step 4
    String hash = Base64.encodeBytes(raw); //step 5
    return hash; //step 6
  }
  public static synchronized PasswordService getInstance() //step 1
  {
    if(instance == null)
    {
      return new PasswordService();
    } 
    else    
    {
      return instance;
    }
  }
}
