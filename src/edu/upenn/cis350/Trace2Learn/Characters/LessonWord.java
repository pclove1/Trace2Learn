package edu.upenn.cis350.Trace2Learn.Characters;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LessonWord {
	
	private List<LessonCharacter> _characters;
	
	private long _id;
	
	private Map<String, Object> _tags;
	
	public LessonWord(){
		_tags = new HashMap<String,Object>();
		_characters = new ArrayList<LessonCharacter>();
	}
	
	public void setId(long id)
	{
		_id = id;
	}
	
	public long getId()
	{
		return _id;
	}
	
	public void addCharacter(LessonCharacter character){
		_characters.add(character);
	}
	
	public List<LessonCharacter> getCharacters(){
		List<LessonCharacter> l = new ArrayList<LessonCharacter>();
		l.addAll(_characters);
		return l;
	}
	
	public LessonCharacter getCharacter(int i){
		return _characters.get(i);
	}
	
	public boolean removeCharacter(LessonCharacter character){
		return _characters.remove(character);
	}
	
	public LessonCharacter removeCharacter(int i){
		return _characters.remove(i);
	}
	
	public void clearCharacters(){
		_characters.clear();
	}
	
	public boolean hasTag(String tag)
	{
		return _tags.containsKey(tag);
	}
	
	public void setTag(String tag, Object value)
	{
		_tags.put(tag, value);
	}
	
	public Object getTag(String tag)
	{
		return _tags.get(tag);
	}
	
	public List<String> getTagNames()
	{
		return new ArrayList<String>(_tags.keySet());
	}
	
}