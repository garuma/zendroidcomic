package org.neteril.Zendroidcomic;

import android.graphics.Bitmap;

public class ComicInformations {
	String comicName;
	String comicAuthor;
	String comicUrl;
	Bitmap comic;
	
	public ComicInformations (String comicName, String comicAuthor, String comicUrl, Bitmap bmp) {
		this.comicName = comicName;
		this.comicAuthor = comicAuthor;
		this.comicUrl = comicUrl;
		this.comic = bmp;
	}
	
	public String getName () {
		return comicName;
	}
	public void setName (String value) {
		comicName = value;
	}
	
	public String getAuthor () {
		return comicAuthor;
	}
	public void setAuthor (String value) {
		comicAuthor = value;
	}
	
	public String getUrl () {
		return comicUrl;
	}
	public void setUrl (String value) {
		comicUrl = value;
	}
	
	public Bitmap getComic () {
		return comic;
	}
	public void setComic (Bitmap value) {
		comic = value;
	}
}
