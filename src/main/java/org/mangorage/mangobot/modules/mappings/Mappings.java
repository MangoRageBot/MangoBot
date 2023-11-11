/*
 * Copyright (c) 2023. MangoRage
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
 * EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
 * MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT.
 * IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM,
 * DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR
 * OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE
 * OR OTHER DEALINGS IN THE SOFTWARE.
 */

/*Copied from AssistRemapper
 *  https://pagure.io/AssistRemapper
 *  You are allowed to use this code unconditionally just like AssistRemapper. If you notice a bug please report to AssistRemapper as well
 */
//package com.asbestosstar.assistremapper;

//Important Info  https://pagure.io/FeatureCreep/MappingExFormat
//To Edit Mappings https://pagure.io/FeatureCreep/enigma-mappingex
package org.mangorage.mangobot.modules.mappings;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author rhel
 *
 */
public class Mappings {
	//We soon need to add comments/javadocs as well
		public Map<String, String> classes = new HashMap<String, String>();
		public Map<String, String> defs = new HashMap<String, String>();
		public Map<String, String> vars = new HashMap<String, String>();
		public Map<String, String> params = new HashMap<String, String>();

		public Mappings reverse;
		
		public Mappings() {
			
		}

		
		public Mappings(InputStream pdme)
		{
		System.out.println("Parsing Mappings");
			BufferedReader reader = new BufferedReader(new InputStreamReader(pdme));
            String line;
            int num = 0;
			try {
				while ((line = reader.readLine()) != null) {
				    // Perform your action for each line stripped here
					System.out.println("Line: -> %s".formatted(line));
					String[] row_array = line.split("¶");
					if (num == 0) {
					} else if (row_array[0].equals("Class")) {
						classes.put(row_array[1], row_array[2]);
					} else if (row_array[0].equals("Def")) {
						defs.put(row_array[1], row_array[2]);
					} else if (row_array[0].equals("Var")) {
						vars.put(row_array[1], row_array[2]);
					} else if (row_array[0].equals("Param")) {
						params.put(row_array[3] + "_" + row_array[4], row_array[2]);
					}

					num++;
				}
            // Close the BufferedReader
            reader.close();	
			
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	          parseSubClasses();
			System.out.println("Getting reverse mappings");
			reverse();
			
		}
		
//Does not yet support params		
		public void reverse() {
			// TODO Auto-generated method stub
		
			
			Mappings rev = new Mappings();
//for every entry in def
			for(Map.Entry<String, String> def:defs.entrySet()) {
			//	System.out.println(def.getKey());
			String[] old_class_arr =	java.util.Arrays.copyOfRange(def.getKey().split("\\."), 0, def.getKey().split("\\.").length - 1);
			String old_classname =   String.join(".",old_class_arr)   ;
			
			String new_classname = getClassMappedName(old_classname);
			String des = renameClassesInMethodDescriptor("("+def.getKey().split("\\(")[1]);
		
			String[] divided = def.getKey().split("\\.");
		
                rev.defs.put(new_classname+"."+def.getValue()+des,divided[divided.length-1].split("\\(")[0]);

		//	rev.defs.add()
			
			}
			
			
			for(Map.Entry<String, String> def:vars.entrySet()) {
				
				String old_classname =   String.join(".",java.util.Arrays.copyOfRange(def.getKey().split("\\."), 0, def.getKey().split("\\.").length - 1))   ;
				
				String new_classname = getClassMappedName(old_classname);
				String old_des=def.getKey().split(":")[1];
				String des=this.renameClassesInFieldDescriptor(old_des);
			
				
				String[] divided = def.getKey().split("\\.");
			
				if(divided[divided.length-1].startsWith("$")) {
				//	System.out.println(divided[divided.length-1]);
				}
				
	                rev.vars.put(new_classname+"."+def.getValue()+":"+des,divided[divided.length-1].split(":")[0]);
	           // System.out.println(new_classname+"."+def.getValue()+":"+des+"¶"+divided[divided.length-1].split(":")[0]);
				
			//	rev.defs.add()
				
				}
			
			for(Map.Entry<String, String> def:classes.entrySet()) {
			rev.classes.put(def.getValue(), def.getKey());
			}
			
		this.reverse=rev;	
		}

		
		 public String renameClassesInMethodDescriptor(String methodDescriptor) {
			 String updatedDescriptor = methodDescriptor;
			    Pattern classPattern = Pattern.compile("L([^;]+);");
			    Matcher classMatcher = classPattern.matcher(methodDescriptor);
			    while (classMatcher.find()) {
			        String className = classMatcher.group(1);
			        String updatedClassName = updateClassName(className);

			        updatedDescriptor = updatedDescriptor.replace(className, updatedClassName);
			    }
			    return updatedDescriptor;
		    }
		 
		 
		 private String updateClassName(String className) {
			 
			    return this.getClassMappedName(className.replace("/", ".")).replace(".", "/");
			}
		 
		

		public String getClassMappedName(String original)
		{
			if (classes.get(original) != null) {
			return classes.get(original);
			}else {return original;}
			
		}

		public String getClassUnMappedName(String mapped)
		{
			 for (Map.Entry<String, String> entry : classes.entrySet()) {
		            if (mapped.equals(entry.getValue())) {
		                return entry.getKey();
		            }
		        }
		        return mapped; // Value not found
		 }
		
		
		public String getDefMappedName(String original)
		{
			return defs.getOrDefault(original, original.split("\\.")[original.split("\\.").length-1].split("\\(")[0]);
		}

		public String getVarMappedName(String original)
		{
			return vars.getOrDefault(original, original.split("\\.")[original.split("\\.").length-1].split(":")[0]);
		}
		
		
		/**
		 * @param method_with_descriptor Obfuscated Method Name with Descriptor using . instead of /
		 * @param location starting from 1 where in the method the param is
		 * @return name
		 */
		public String getParamMappedName(String method_with_descriptor, int location)
		{
			if (defs.get(method_with_descriptor+"_"+Integer.toString(location)) != null) {
				return defs.get(method_with_descriptor+"_"+Integer.toString(location));
			}else
			{return method_with_descriptor;}
		}
		
		
		
		  
		  
		  public void parseSubClasses() {
			  for (Map.Entry<String, String> entry : this.classes.entrySet()) {
			if(entry.getKey().contains("$"))
				  entry.setValue(parseSubClass(entry.getKey()));
			  }
		  }
		  
		  
		  
		  public String parseSubClass(String original_classname) {
			    String new_name = getClassMappedName(original_classname);
				  if (new_name.contains("$")) {
					  return new_name;
				  }
				  
				String[] sub_arr = original_classname.split("\\$");

			    String[] subarray = Arrays.copyOfRange(sub_arr, 0, sub_arr.length - 1).clone();
			    // Join the subarray elements with "$"
			    String root_class = getClassMappedName(String.join("$", subarray));
			   if(root_class.contains("$")) {
				   root_class=(parseSubClass(root_class));
			   }
			    
			    String sub_class = new_name;
			    return root_class + "$" + sub_class;

			  }

			  public String parseSubClassUnmapped(String unmapped) {
			    String[] sub_arr = unmapped.split("\\$");

			    String[] subarray = Arrays.copyOfRange(sub_arr, 0, sub_arr.length - 1).clone();
			    // Join the subarray elements with "$"
			    String root_class = getClassMappedName(String.join("$", subarray));
			    String sub_class = getClassMappedName(unmapped);
			    return root_class + "$" + sub_class;

			  }
			  
			  
			  public void addClass(String original, String mapped)
			  {
				  this.classes.put(original, mapped);
			  }
			  
			  public void addDef(String original, String mapped)
			  {
				  this.defs.put(original, mapped);
			  }
			  
			  public void addVar(String original, String mapped)
			  {
				  this.vars.put(original, mapped);
			  }
			  
			  public void addParam(String original, String mapped)
			  {
				  this.params.put(original, mapped);
			  }
			  
			  public String renameClassesInFieldDescriptor(String old_desc) {
				    if (!old_desc.contains("L")) {
				        return old_desc;
				    } else {
				        String[] parts = old_desc.split("L");
				        String[] classNameParts = parts[1].split(";");
				        String old_clazz = classNameParts[0].replaceAll("/", ".");
				        String clazz = getClassMappedName(old_clazz).replaceAll("\\.", "/");
				        String desc = old_desc.replaceAll(old_clazz.replaceAll("\\.", "/"), clazz);
				        return desc;
				    }
				}
		
}
