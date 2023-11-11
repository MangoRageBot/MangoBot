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

/*FeatureCreep Moderation Bot Mapping Module
 * Ported to MangoBot from Ruby by FeatureCreep Team
 *  https://pagure.io/FeatureCreep/featurecreep-moderation-bot
 *  You are allowed to use this code unconditionally just like FCMB
 */

package org.mangorage.mangobot.modules.mappings;

import net.dv8tion.jda.api.entities.Message;
import org.mangorage.basicutils.config.ISetting;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class MappingsManager {

  public Mappings SRG;
  public Mappings SUGARCANE;
  public Mappings YARN;
  public Mappings FABRIC_INTERMEDIARY;
  //We do not need for FeatureCreep Intermediary since it is the backend

  /*
   * To be like Ruby
   * */
  public static MappingsManager new_() {
    return new MappingsManager();
  }

  public byte[] download(URL url) {
    try {
      URLConnection connection = url.openConnection();
      // Download the file and convert it to a byte array
      try (InputStream inputStream = connection.getInputStream(); ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
        byte[] buffer = new byte[1024];
        int length;
        while ((length = inputStream.read(buffer)) != -1) {
          outputStream.write(buffer, 0, length);
        }
        byte[] fileContents = outputStream.toByteArray();
        return fileContents;
        // Do something with the file contents
      }
    } catch (IOException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
    return new byte[0];
  }

  //MangoBot Specific
  private String get_mappings() {
    // TODO Auto-generated method stub
    String latest = "";
    System.out.println("Checking for latest version of Mappings and Downloading them");
    try {
      URL version_url = new URL("https://pagure.io/FeatureCreep/featurecreep-moderation-bot/raw/main/f/mappings/latest");
      String version = new String(download(version_url), StandardCharsets.UTF_8);
      latest = version;
      String mc_version = version.split("-")[0];
      File dir = new File("mappings/" + mc_version + "/");
      dir.mkdirs();
      URL url = new URL("https://pagure.io/FeatureCreep/featurecreep-moderation-bot/raw/main/f/mappings/" + mc_version + "/todos.zip");
      byte[] zip = download(url);
      try (ZipInputStream zis = new ZipInputStream(new ByteArrayInputStream(zip))) {
        ZipEntry entry = zis.getNextEntry();

        while (entry != null) {
          String fileName = entry.getName();
          File file = new File(dir + "/" + fileName);

          if (entry.isDirectory()) {
            file.mkdirs();
          } else {
            file.getParentFile().mkdirs();

            try (FileOutputStream fos = new FileOutputStream(file)) {
              int len;
              byte[] buffer = new byte[1024];
              while ((len = zis.read(buffer)) > 0) {
                fos.write(buffer, 0, len);
              }
            }
          }

          zis.closeEntry();
          entry = zis.getNextEntry();
        }
      }

    } catch (IOException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }

    return latest;
  }

  private String checkForUpdates(ISetting<String> conf) {
    // TODO Auto-generated method stub
    System.out.println("Checking for Mapping Updates");
    try {
      URL version_url = new URL("https://pagure.io/FeatureCreep/featurecreep-moderation-bot/raw/main/f/mappings/latest");
      Path version_path = Paths.get(version_url.toURI());
      String version = new String(Files.readAllBytes(version_path), StandardCharsets.UTF_8);
      if (version.compareTo(conf.get()) > 0) {
        System.out.println("Mapping update found. Downloading");
        return get_mappings();
      }

    } catch (URISyntaxException | IOException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }

    return conf.get();
  }

  public void init(ISetting<String> conf) {

    String latest;
    if (conf.get().equals("empty")) {
      latest = get_mappings().split("-")[0];
      conf.set(latest);
    } else {
      String current = conf.get().split("-")[0]; //after - is FeatureCreep Version
      latest = checkForUpdates(conf);
    }

    try {
      this.SRG = new Mappings(new FileInputStream(new File("mappings/" + latest + "/srg.pdme")));
      this.SUGARCANE = new Mappings(new FileInputStream(new File("mappings/" + latest + "/sugarcane.pdme")));
      this.YARN = new Mappings(new FileInputStream(new File("mappings/" + latest + "/yarn.pdme")));
      this.FABRIC_INTERMEDIARY = new Mappings(new FileInputStream(new File("mappings/" + latest + "/fabric-intermediary.pdme")));
    } catch (FileNotFoundException e) {
      // TODO Auto-generated catch block
      this.SRG = new Mappings();
      this.SUGARCANE = new Mappings();
      this.YARN = new Mappings();
      this.FABRIC_INTERMEDIARY = new Mappings();
      SRG.reverse = new Mappings();
      SUGARCANE.reverse = new Mappings();
      YARN.reverse = new Mappings();
      FABRIC_INTERMEDIARY.reverse = new Mappings();

      e.printStackTrace();
    }

  }

  public Mappings getSRG() {
    return this.SRG;
  }

  public Mappings getSUGARCANE() {
    return this.SUGARCANE;
  }

  public Mappings getYARN() {
    return this.YARN;
  }

  public Mappings getFABRIC_INTERMEDIARY() {
    return this.FABRIC_INTERMEDIARY;
  }

  public String fciclassfind(String fciName) {
    // code here
    fciName = fciName.replace("/", ".");
    boolean isPackage = fciName.contains(".");
    List < String > targets = new ArrayList < > ();
    List < String > outputs = new ArrayList < > ();
    if (isPackage && FABRIC_INTERMEDIARY.reverse.classes.containsKey(fciName)) {
      targets.add(fciName);
    } else {
      for (Entry < String, String > entry: FABRIC_INTERMEDIARY.classes.entrySet()) {
        if (entry.getValue().contains(fciName)) {
          targets.add(entry.getValue());
        }
      }
    }

    for (String target: targets) {
      String output = "FeatureCreep Intermediary: `" + target + "`\n";
      if (SUGARCANE.reverse.classes.containsKey(target)) {
        // They Should not have the same name in most cases
        String sugarcaneOutput = SUGARCANE.reverse.getClassMappedName(target);
        output += "MojMap: `" + sugarcaneOutput + "`\n";
      }
      if (YARN.reverse.classes.containsKey(target)) {
        // They Should not have the same name in most cases
        String yarnOutput = YARN.reverse.getClassMappedName(target);
        output += "Yarn: `" + yarnOutput + "`\n";
      }
      if (SRG.reverse.classes.containsKey(target)) {
        // They Should not have the same name in most cases
        String srgOutput = SRG.reverse.getClassMappedName(target);
        output += "SRG: `" + srgOutput + "`\n";
      }
      if (FABRIC_INTERMEDIARY.reverse.classes.containsKey(target)) {
        // They Should not have the same name in most cases
        String fainOutput = FABRIC_INTERMEDIARY.reverse.getClassMappedName(target);
        output += "Fabric Intermediary: `" + fainOutput + "`\n";
      }

      outputs.add(output);
    }

    if (outputs.isEmpty()) {
      String out = "No class mapping found for " + fciName + ". Make sure it's in the correct format using . instead of / in the main class (but not descriptors) and that the class is actually mapped and not the same on most mappings and that you are on the latest version and that the mapping DB has updated to the newest version.";
      return out;
    }

    String result = String.join("\n", outputs);
    if (result.length() > 2000) {
      result = "Output too long. Please Narrow your Search";
    }
    return result;
  }

  public String srgclassfind(String srg_name) {
    // Replace all slashes with periods
    srg_name = srg_name.replace("/", ".");
    // Check if the input is a package
    boolean pack = srg_name.contains(".");

    // Initialize arrays to store targets and outputs
    ArrayList < String > targets = new ArrayList < > ();
    ArrayList < String > outputs = new ArrayList < > ();

    // Loop through the classes in the SRG mapping and add any targets that match the input to the targets array

    if (pack && SRG.classes.containsKey(srg_name)) {
      targets.add(srg_name);
    } else {
      for (Entry < String, String > entry: SRG.classes.entrySet()) {

        if (entry.getKey().contains(srg_name)) {
          targets.add(entry.getKey());
        }

      }
    }

    // Loop through the targets and generate output for each one
    for (String target: targets) {
      String output = "**SRG:** `" + target + "`\n";

      // Check if the target is mapped to a class in the FCI mapping
      if (SRG.classes.containsKey(target)) {
        String fci_output = SRG.getClassMappedName(target);

        // Add the FCI mapping to the output
        output += "**FeatureCreep Intermediary:** `" + fci_output + "`\n";

        // Check if the FCI mapping is mapped to a class in the SUGARCANE mapping
        if (SUGARCANE.reverse.classes.containsKey(fci_output)) {
          String sugarcane_output = SUGARCANE.reverse.getClassMappedName(fci_output);
          output += "**MojMap:** `" + sugarcane_output + "`\n";
        }

        // Check if the FCI mapping is mapped to a class in the YARN mapping
        if (YARN.reverse.classes.containsKey(fci_output)) {
          String yarn_output = YARN.reverse.getClassMappedName(fci_output);
          output += "**Yarn:** `" + yarn_output + "`\n";
        }

        // Check if the FCI mapping is mapped to a class in the FABRIC_INTERMEDIARY mapping
        if (FABRIC_INTERMEDIARY.reverse.classes.containsKey(fci_output)) {
          String fain_output = FABRIC_INTERMEDIARY.reverse.getClassMappedName(fci_output);
          output += "**Fabric Intermediary:** `" + fain_output + "`\n";
        }
      }

      // Add the output to the outputs array
      outputs.add(output);
    }

    // Check if there are any outputs
    if (outputs.isEmpty()) {
      return "No class mapping found for " + srg_name + ". Make sure it's in the correct format using . instead of / in the main class (but not descriptors) and that the class is actually mapped and not the same on most mappings and that you are on the latest version and that the mapping DB has updated to the newest version.";
    }

    // Join the outputs and return the result
    return String.join("\n", outputs);

  }

  public String yarnclassfind(String yarn_name) {
    // Replace all slashes with periods
    yarn_name = yarn_name.replace("/", ".");
    // Check if the input is a package
    boolean pack = yarn_name.contains(".");

    // Initialize arrays to store targets and outputs
    ArrayList < String > targets = new ArrayList < > ();
    ArrayList < String > outputs = new ArrayList < > ();

    if (pack && YARN.classes.containsKey(yarn_name)) {
      targets.add(yarn_name);
    } else {
      for (Entry < String, String > entry: YARN.classes.entrySet()) {

        if (entry.getKey().contains(yarn_name)) {
          targets.add(entry.getKey());
        }

      }
    }

    // Loop through the targets and generate output for each one
    for (String target: targets) {
      String output = "**Yarn:** `" + target + "`\n";

      // Check if the target is mapped to a class in the FCI mapping
      if (YARN.classes.containsKey(target)) {
        String fci_output = YARN.getClassMappedName(target);

        // Add the FCI mapping to the output
        output += "**FeatureCreep Intermediary:** `" + fci_output + "`\n";

        // Check if the FCI mapping is mapped to a class in the SUGARCANE mapping
        if (SUGARCANE.reverse.classes.containsKey(fci_output)) {
          String sugarcane_output = SUGARCANE.reverse.getClassMappedName(fci_output);
          output += "**MojMap:** `" + sugarcane_output + "`\n";
        }

        // Check if the FCI mapping is mapped to a class in the YARN mapping
        if (SRG.reverse.classes.containsKey(fci_output)) {
          String yarn_output = SRG.reverse.getClassMappedName(fci_output);
          output += "**SRG:** `" + yarn_output + "`\n";
        }

        // Check if the FCI mapping is mapped to a class in the FABRIC_INTERMEDIARY mapping
        if (FABRIC_INTERMEDIARY.reverse.classes.containsKey(fci_output)) {
          String fain_output = FABRIC_INTERMEDIARY.reverse.getClassMappedName(fci_output);
          output += "**Fabric Intermediary:** `" + fain_output + "`\n";
        }
      }

      // Add the output to the outputs array
      outputs.add(output);
    }

    // Check if there are any outputs
    if (outputs.isEmpty()) {
      return "No class mapping found for " + yarn_name + ". Make sure it's in the correct format using . instead of / in the main class (but not descriptors) and that the class is actually mapped and not the same on most mappings and that you are on the latest version and that the mapping DB has updated to the newest version.";
    }

    // Join the outputs and return the result
    return String.join("\n", outputs);

  }

  public String sugarcaneclassfind(String moj_name) {
    // Replace all slashes with periods
    moj_name = moj_name.replace("/", ".");
    // Check if the input is a package
    boolean pack = moj_name.contains(".");

    // Initialize arrays to store targets and outputs
    ArrayList < String > targets = new ArrayList < > ();
    ArrayList < String > outputs = new ArrayList < > ();

    if (pack && SUGARCANE.classes.containsKey(moj_name)) {
      targets.add(moj_name);
    } else {
      for (Entry < String, String > entry: SUGARCANE.classes.entrySet()) {

        if (entry.getKey().contains(moj_name)) {
          targets.add(entry.getKey());
        }

      }
    }

    // Loop through the targets and generate output for each one
    for (String target: targets) {
      String output = "**MojMap:** `" + target + "`\n";

      // Check if the target is mapped to a class in the FCI mapping
      if (SUGARCANE.classes.containsKey(target)) {
        String fci_output = SUGARCANE.getClassMappedName(target);

        // Add the FCI mapping to the output
        output += "**FeatureCreep Intermediary:** `" + fci_output + "`\n";

        // Check if the FCI mapping is mapped to a class in the SUGARCANE mapping
        if (YARN.reverse.classes.containsKey(fci_output)) {
          String sugarcane_output = YARN.reverse.getClassMappedName(fci_output);
          output += "**Yarn:** `" + sugarcane_output + "`\n";
        }

        // Check if the FCI mapping is mapped to a class in the YARN mapping
        if (SRG.reverse.classes.containsKey(fci_output)) {
          String yarn_output = SRG.reverse.getClassMappedName(fci_output);
          output += "**SRG:** `" + yarn_output + "`\n";
        }

        // Check if the FCI mapping is mapped to a class in the FABRIC_INTERMEDIARY mapping
        if (FABRIC_INTERMEDIARY.reverse.classes.containsKey(fci_output)) {
          String fain_output = FABRIC_INTERMEDIARY.reverse.getClassMappedName(fci_output);
          output += "**Fabric Intermediary:** `" + fain_output + "`\n";
        }
      }

      // Add the output to the outputs array
      outputs.add(output);
    }

    // Check if there are any outputs
    if (outputs.isEmpty()) {
      return "No class mapping found for " + moj_name + ". Make sure it's in the correct format using . instead of / in the main class (but not descriptors) and that the class is actually mapped and not the same on most mappings and that you are on the latest version and that the mapping DB has updated to the newest version.";
    }

    // Join the outputs and return the result
    return String.join("\n", outputs);

  }

  public String fainclassfind(String fain_name) {
    // Replace all slashes with periods
    fain_name = fain_name.replace("/", ".");
    // Check if the input is a package
    boolean pack = fain_name.contains(".");

    // Initialize arrays to store targets and outputs
    ArrayList < String > targets = new ArrayList < > ();
    ArrayList < String > outputs = new ArrayList < > ();

    if (pack && FABRIC_INTERMEDIARY.classes.containsKey(fain_name)) {
      targets.add(fain_name);
    } else {
      for (Entry < String, String > entry: FABRIC_INTERMEDIARY.classes.entrySet()) {

        if (entry.getKey().contains(fain_name)) {
          targets.add(entry.getKey());
        }

      }
    }

    // Loop through the targets and generate output for each one
    for (String target: targets) {
      String output = "**Fabric Intermediary:** `" + target + "`\n";

      // Check if the target is mapped to a class in the FCI mapping
      if (FABRIC_INTERMEDIARY.classes.containsKey(target)) {
        String fci_output = FABRIC_INTERMEDIARY.getClassMappedName(target);

        // Add the FCI mapping to the output
        output += "**FeatureCreep Intermediary:** `" + fci_output + "`\n";

        // Check if the FCI mapping is mapped to a class in the SUGARCANE mapping
        if (SUGARCANE.reverse.classes.containsKey(fci_output)) {
          String sugarcane_output = SUGARCANE.reverse.getClassMappedName(fci_output);
          output += "**MojMap:** `" + sugarcane_output + "`\n";
        }

        // Check if the FCI mapping is mapped to a class in the YARN mapping
        if (SRG.reverse.classes.containsKey(fci_output)) {
          String yarn_output = SRG.reverse.getClassMappedName(fci_output);
          output += "**SRG:** `" + yarn_output + "`\n";
        }

        // Check if the FCI mapping is mapped to a class in the FABRIC_INTERMEDIARY mapping
        if (YARN.reverse.classes.containsKey(fci_output)) {
          String fain_output = YARN.reverse.getClassMappedName(fci_output);
          output += "**Yarn:** `" + fain_output + "`\n";
        }
      }

      // Add the output to the outputs array
      outputs.add(output);
    }

    // Check if there are any outputs
    if (outputs.isEmpty()) {
      return "No class mapping found for " + fain_name + ". Make sure it's in the correct format using . instead of / in the main class (but not descriptors) and that the class is actually mapped and not the same on most mappings and that you are on the latest version and that the mapping DB has updated to the newest version.";
    }

    // Join the outputs and return the result
    return String.join("\n", outputs);

  }

  public String srgdeffind(String srg_name) {
    String out;
    boolean pack = srg_name.contains(".");
    ArrayList < String > targets = new ArrayList < String > ();
    ArrayList < String > outputs = new ArrayList < String > ();
    if (pack && SRG.defs.containsKey(srg_name)) {
      targets.add(srg_name);
    } else {
      for (Entry < String, String > entry: SRG.defs.entrySet()) {
        if (entry.getKey().contains(srg_name)) {
          targets.add(entry.getKey());
        }
      }
    }

    for (String target: targets) {
      StringBuilder output = new StringBuilder("**SRG:** `" + target + "`\n");
      if (SRG.defs.containsKey(target)) { // They Should not have the same name in most cases
        String fci_output = SRG.getDefMappedName(target);
        String[] old_clazz_old = target.split("\\.");
        String[] old_clazz = Arrays.copyOf(old_clazz_old, old_clazz_old.length - 1);
        String clazz = SRG.getClassMappedName(String.join(".", old_clazz));
        String old_desc = target.split("\\(")[1];
        String desc = SRG.renameClassesInMethodDescriptor(old_desc);
        fci_output = fci_output + "(" + desc;
        output.append("**FeatureCreep Intermediary:** `" + fci_output + "`\n");
        String fci_output_temp = clazz + "." + fci_output;
        if (SUGARCANE.reverse.defs.containsKey(fci_output_temp)) { // They Should not have the same name in most cases
          String sugarcane_output = SUGARCANE.reverse.getDefMappedName(fci_output_temp);
          String old_desc_temp = fci_output_temp.split("\\(")[1];
          String desc_temp = SUGARCANE.reverse.renameClassesInMethodDescriptor(old_desc_temp);
          output.append("**MojMap:** `" + sugarcane_output + "(" + desc_temp + "`\n");
        }
        if (YARN.reverse.defs.containsKey(fci_output_temp)) { // They Should not have the same name in most cases
          String yarn_output = YARN.reverse.getDefMappedName(fci_output_temp);
          String old_desc_temp = fci_output_temp.split("\\(")[1];
          String desc_temp = YARN.reverse.renameClassesInMethodDescriptor(old_desc_temp);
          output.append("**Yarn:** `" + yarn_output + "(" + desc_temp + "`\n");
        }
        if (FABRIC_INTERMEDIARY.reverse.defs.containsKey(fci_output_temp)) { // They Should not have the same name in most cases
          String fain_output = FABRIC_INTERMEDIARY.reverse.getDefMappedName(fci_output_temp);
          String old_desc_temp = fci_output_temp.split("\\(")[1];
          String desc_temp = FABRIC_INTERMEDIARY.reverse.renameClassesInMethodDescriptor(old_desc_temp);
          output.append("**Fabric Intermediary:** `" + fain_output + "(" + desc_temp + "`\n");
        }

        HashMap < String, String > args = new HashMap < String, String > ();
        for (Entry < String, String > entry: SRG.params.entrySet()) {
          if (entry.getKey().contains(target)) { // Not perfect buy ok for now
            String name = entry.getValue();
            String loc = entry.getKey().split("_")[entry.getKey().split("_").length - 1];
            args.put(loc, name);
          }
        }
        if (!args.isEmpty()) {
          output.append("**FCI Args:** `" + args.toString() + "`\n");
        }

      }
      outputs.add(output.toString());
    }

    if (outputs.isEmpty()) {
      out = "No mapping found for " + srg_name + ". Make sure its in the correct format using . instead of / in the main class (but not descriptors) and that the class is actually mapped and not the same on most mappings and that you are on the latest version and that the mapping DB has updated to the newest version.";
    } else {
      out = String.join("\n", outputs);
      if (out.length() > 2000) {
        out = "Output too long. Please Narrow your Search. If the name is short and you have the full name but not descriptor add ( to the end";
      }
    }
    return out;
  }

  public String sugarcanedeffind(String moj_name) {
    String out;
    boolean pack = moj_name.contains(".");
    ArrayList < String > targets = new ArrayList < String > ();
    ArrayList < String > outputs = new ArrayList < String > ();
    if (pack && SUGARCANE.defs.containsKey(moj_name)) {
      targets.add(moj_name);
    } else {
      for (Entry < String, String > entry: SUGARCANE.defs.entrySet()) {
        if (entry.getKey().contains(moj_name)) {
          targets.add(entry.getKey());
        }
      }
    }

    for (String target: targets) {
      StringBuilder output = new StringBuilder("**MojMap:** `" + target + "`\n");
      if (SUGARCANE.defs.containsKey(target)) { // They Should not have the same name in most cases
        String fci_output = SUGARCANE.getDefMappedName(target);
        String[] old_clazz_old = target.split("\\.");
        String[] old_clazz = Arrays.copyOf(old_clazz_old, old_clazz_old.length - 1);
        String clazz = SUGARCANE.getClassMappedName(String.join(".", old_clazz));
        String old_desc = target.split("\\(")[1];
        String desc = SUGARCANE.renameClassesInMethodDescriptor(old_desc);
        fci_output = fci_output + "(" + desc;
        output.append("**FeatureCreep Intermediary:** `" + fci_output + "`\n");
        String fci_output_temp = clazz + "." + fci_output;
        if (SRG.reverse.defs.containsKey(fci_output_temp)) { // They Should not have the same name in most cases
          String sugarcane_output = SRG.reverse.getDefMappedName(fci_output_temp);
          String old_desc_temp = fci_output_temp.split("\\(")[1];
          String desc_temp = SRG.reverse.renameClassesInMethodDescriptor(old_desc_temp);
          output.append("**SRG:** `" + sugarcane_output + "(" + desc_temp + "`\n");
        }
        if (YARN.reverse.defs.containsKey(fci_output_temp)) { // They Should not have the same name in most cases
          String yarn_output = YARN.reverse.getDefMappedName(fci_output_temp);
          String old_desc_temp = fci_output_temp.split("\\(")[1];
          String desc_temp = YARN.reverse.renameClassesInMethodDescriptor(old_desc_temp);
          output.append("**Yarn:** `" + yarn_output + "(" + desc_temp + "`\n");
        }
        if (FABRIC_INTERMEDIARY.reverse.defs.containsKey(fci_output_temp)) { // They Should not have the same name in most cases
          String fain_output = FABRIC_INTERMEDIARY.reverse.getDefMappedName(fci_output_temp);
          String old_desc_temp = fci_output_temp.split("\\(")[1];
          String desc_temp = FABRIC_INTERMEDIARY.reverse.renameClassesInMethodDescriptor(old_desc_temp);
          output.append("**Fabric Intermediary:** `" + fain_output + "(" + desc_temp + "`\n");
        }

        HashMap < String, String > args = new HashMap < String, String > ();
        for (Entry < String, String > entry: SUGARCANE.params.entrySet()) {
          if (entry.getKey().contains(target)) { // Not perfect buy ok for now
            String name = entry.getValue();
            String loc = entry.getKey().split("_")[entry.getKey().split("_").length - 1];
            args.put(loc, name);
          }
        }
        if (!args.isEmpty()) {
          output.append("**FCI Args:** `" + args.toString() + "`\n");
        }

      }
      outputs.add(output.toString());
    }

    if (outputs.isEmpty()) {
      out = "No mapping found for " + moj_name + ". Make sure its in the correct format using . instead of / in the main class (but not descriptors) and that the class is actually mapped and not the same on most mappings and that you are on the latest version and that the mapping DB has updated to the newest version.";
    } else {
      out = String.join("\n", outputs);
      if (out.length() > 2000) {
        out = "Output too long. Please Narrow your Search. If the name is short and you have the full name but not descriptor add ( to the end";
      }
    }
    return out;
  }

  public String faindeffind(String fain_name) {
    String out;
    boolean pack = fain_name.contains(".");
    ArrayList < String > targets = new ArrayList < String > ();
    ArrayList < String > outputs = new ArrayList < String > ();
    if (pack && SRG.defs.containsKey(fain_name)) {
      targets.add(fain_name);
    } else {
      for (Entry < String, String > entry: FABRIC_INTERMEDIARY.defs.entrySet()) {
        if (entry.getKey().contains(fain_name)) {
          targets.add(entry.getKey());
        }
      }
    }

    for (String target: targets) {
      StringBuilder output = new StringBuilder("**Fabric Intermediary:** `" + target + "`\n");
      if (FABRIC_INTERMEDIARY.defs.containsKey(target)) { // They Should not have the same name in most cases
        String fci_output = FABRIC_INTERMEDIARY.getDefMappedName(target);
        String[] old_clazz_old = target.split("\\.");
        String[] old_clazz = Arrays.copyOf(old_clazz_old, old_clazz_old.length - 1);
        String clazz = FABRIC_INTERMEDIARY.getClassMappedName(String.join(".", old_clazz));
        String old_desc = target.split("\\(")[1];
        String desc = FABRIC_INTERMEDIARY.renameClassesInMethodDescriptor(old_desc);
        fci_output = fci_output + "(" + desc;
        output.append("**FeatureCreep Intermediary:** `" + fci_output + "`\n");
        String fci_output_temp = clazz + "." + fci_output;
        if (SUGARCANE.reverse.defs.containsKey(fci_output_temp)) { // They Should not have the same name in most cases
          String sugarcane_output = SUGARCANE.reverse.getDefMappedName(fci_output_temp);
          String old_desc_temp = fci_output_temp.split("\\(")[1];
          String desc_temp = SUGARCANE.reverse.renameClassesInMethodDescriptor(old_desc_temp);
          output.append("**MojMap:** `" + sugarcane_output + "(" + desc_temp + "`\n");
        }
        if (YARN.reverse.defs.containsKey(fci_output_temp)) { // They Should not have the same name in most cases
          String yarn_output = YARN.reverse.getDefMappedName(fci_output_temp);
          String old_desc_temp = fci_output_temp.split("\\(")[1];
          String desc_temp = YARN.reverse.renameClassesInMethodDescriptor(old_desc_temp);
          output.append("**Yarn:** `" + yarn_output + "(" + desc_temp + "`\n");
        }
        if (SRG.reverse.defs.containsKey(fci_output_temp)) { // They Should not have the same name in most cases
          String fain_output = SRG.reverse.getDefMappedName(fci_output_temp);
          String old_desc_temp = fci_output_temp.split("\\(")[1];
          String desc_temp = SRG.reverse.renameClassesInMethodDescriptor(old_desc_temp);
          output.append("**SRG:** `" + fain_output + "(" + desc_temp + "`\n");
        }

        HashMap < String, String > args = new HashMap < String, String > ();
        for (Entry < String, String > entry: FABRIC_INTERMEDIARY.params.entrySet()) {
          if (entry.getKey().contains(target)) { // Not perfect buy ok for now
            String name = entry.getValue();
            String loc = entry.getKey().split("_")[entry.getKey().split("_").length - 1];
            args.put(loc, name);
          }
        }
        if (!args.isEmpty()) {
          output.append("**FCI Args:** `" + args.toString() + "`\n");
        }

      }
      outputs.add(output.toString());
    }

    if (outputs.isEmpty()) {
      out = "No mapping found for " + fain_name + ". Make sure its in the correct format using . instead of / in the main class (but not descriptors) and that the class is actually mapped and not the same on most mappings and that you are on the latest version and that the mapping DB has updated to the newest version.";
    } else {
      out = String.join("\n", outputs);
      if (out.length() > 2000) {
        out = "Output too long. Please Narrow your Search. If the name is short and you have the full name but not descriptor add ( to the end";
      }
    }
    return out;
  }

  public String yarndeffind(String yarn_name) {
    String out;
    boolean pack = yarn_name.contains(".");
    ArrayList < String > targets = new ArrayList < String > ();
    ArrayList < String > outputs = new ArrayList < String > ();
    if (pack && YARN.defs.containsKey(yarn_name)) {
      targets.add(yarn_name);
    } else {
      for (Entry < String, String > entry: YARN.defs.entrySet()) {
        if (entry.getKey().contains(yarn_name)) {
          targets.add(entry.getKey());
        }
      }
    }

    for (String target: targets) {
      StringBuilder output = new StringBuilder("**Yarn:** `" + target + "`\n");
      if (YARN.defs.containsKey(target)) { // They Should not have the same name in most cases
        String fci_output = YARN.getDefMappedName(target);
        String[] old_clazz_old = target.split("\\.");
        String[] old_clazz = Arrays.copyOf(old_clazz_old, old_clazz_old.length - 1);
        String clazz = YARN.getClassMappedName(String.join(".", old_clazz));
        String old_desc = target.split("\\(")[1];
        String desc = YARN.renameClassesInMethodDescriptor(old_desc);
        fci_output = fci_output + "(" + desc;
        output.append("**FeatureCreep Intermediary:** `" + fci_output + "`\n");
        String fci_output_temp = clazz + "." + fci_output;
        if (SRG.reverse.defs.containsKey(fci_output_temp)) { // They Should not have the same name in most cases
          String sugarcane_output = SRG.reverse.getDefMappedName(fci_output_temp);
          String old_desc_temp = fci_output_temp.split("\\(")[1];
          String desc_temp = SRG.reverse.renameClassesInMethodDescriptor(old_desc_temp);
          output.append("**SRG:** `" + sugarcane_output + "(" + desc_temp + "`\n");
        }
        if (SUGARCANE.reverse.defs.containsKey(fci_output_temp)) { // They Should not have the same name in most cases
          String yarn_output = SUGARCANE.reverse.getDefMappedName(fci_output_temp);
          String old_desc_temp = fci_output_temp.split("\\(")[1];
          String desc_temp = SUGARCANE.reverse.renameClassesInMethodDescriptor(old_desc_temp);
          output.append("**MojMap:** `" + yarn_output + "(" + desc_temp + "`\n");
        }
        if (FABRIC_INTERMEDIARY.reverse.defs.containsKey(fci_output_temp)) { // They Should not have the same name in most cases
          String fain_output = FABRIC_INTERMEDIARY.reverse.getDefMappedName(fci_output_temp);
          String old_desc_temp = fci_output_temp.split("\\(")[1];
          String desc_temp = FABRIC_INTERMEDIARY.reverse.renameClassesInMethodDescriptor(old_desc_temp);
          output.append("**Fabric Intermediary:** `" + fain_output + "(" + desc_temp + "`\n");
        }

        HashMap < String, String > args = new HashMap < String, String > ();
        for (Entry < String, String > entry: YARN.params.entrySet()) {
          if (entry.getKey().contains(target)) { // Not perfect buy ok for now
            String name = entry.getValue();
            String loc = entry.getKey().split("_")[entry.getKey().split("_").length - 1];
            args.put(loc, name);
          }
        }
        if (!args.isEmpty()) {
          output.append("**FCI Args:** `" + args.toString() + "`\n");
        }

      }
      outputs.add(output.toString());
    }

    if (outputs.isEmpty()) {
      out = "No mapping found for " + yarn_name + ". Make sure its in the correct format using . instead of / in the main class (but not descriptors) and that the class is actually mapped and not the same on most mappings and that you are on the latest version and that the mapping DB has updated to the newest version.";
    } else {
      out = String.join("\n", outputs);
      if (out.length() > 2000) {
        out = "Output too long. Please Narrow your Search. If the name is short and you have the full name but not descriptor add ( to the end";
      }
    }
    return out;
  }

  public String fcideffind(String fci_name) {
    // code here
    boolean pack = fci_name.contains("(");
    List < String > targets = new ArrayList < String > ();
    List < String > outputs = new ArrayList < String > ();
    if (pack && FABRIC_INTERMEDIARY.reverse.defs.containsKey(fci_name)) {
      targets.add(fci_name);
    } else {
      for (Entry < String, String > entry: FABRIC_INTERMEDIARY.defs.entrySet()) {
        if (entry.getValue().contains(fci_name)) {
          String[] splitKeys = entry.getKey().split("\\.");
          String old_clazz = String.join(".", Arrays.copyOfRange(splitKeys, 0, splitKeys.length - 1));
          String clazz = FABRIC_INTERMEDIARY.getClassMappedName(old_clazz);
          String old_desc = "(" + entry.getKey().split("\\(")[1];
          String desc = FABRIC_INTERMEDIARY.renameClassesInMethodDescriptor(old_desc);
          targets.add(clazz + "." + entry.getValue() + desc);
        }
      }
    }

    for (String target: targets) {
      String output = "**FeatureCreep Intermediary:** `" + target + "`\n";
      if (SUGARCANE.reverse.defs.containsKey(target)) {
        String sugarcane_output = SUGARCANE.reverse.getDefMappedName(target);
        String old_desc = "(" + target.split("\\(")[1];
        String desc = SUGARCANE.reverse.renameClassesInMethodDescriptor(old_desc);
        output += "**MojMap:** `" + sugarcane_output + desc + "`\n";
      }
      if (YARN.reverse.defs.containsKey(target)) {
        String yarn_output = YARN.reverse.getDefMappedName(target);
        String old_desc = "(" + target.split("\\(")[1];
        String desc = YARN.reverse.renameClassesInMethodDescriptor(old_desc);
        output += "**Yarn:** `" + yarn_output + "(" + desc + "`\n";
      }
      if (SRG.reverse.defs.containsKey(target)) {
        String srg_output = SRG.reverse.getDefMappedName(target);
        String old_desc = "(" + target.split("\\(")[1];
        String desc = SRG.reverse.renameClassesInMethodDescriptor(old_desc);
        output += "**SRG:** `" + srg_output + "(" + desc + "`\n";
      }
      if (FABRIC_INTERMEDIARY.reverse.defs.containsKey(target)) {
        String fain_output = FABRIC_INTERMEDIARY.reverse.getDefMappedName(target);
        String old_desc = "(" + target.split("\\(")[1];
        String desc = FABRIC_INTERMEDIARY.reverse.renameClassesInMethodDescriptor(old_desc);
        output += "**Fabric Intermediary:** `" + fain_output + "(" + desc + "`\n";
      }

      Map < String, String > args = new HashMap < String, String > ();
      for (Entry < String, String > entry: FABRIC_INTERMEDIARY.params.entrySet()) {
        if (entry.getKey().contains(FABRIC_INTERMEDIARY.reverse.getDefMappedName(target))) {
          String name = entry.getValue();
          String loc = entry.getKey().split("_")[1];
          args.put(loc, name);
        }
      }
      if (!args.isEmpty()) {
        output += "**FCI Args:** `" + args.toString() + "`\n";
      }

      outputs.add(output);
    }

    String out = "";
    if (outputs.isEmpty()) {
      out = "No mapping found for " + fci_name + ". Make sure its in the correct format using . instead of / in the main class (but not descriptors) and that the class is actually mapped and not the same on most mappings and that you are on the latest version and that the mapping DB has updated to the newest version.";
    } else {
      out = String.join("\n", outputs);
      if (out.length() > 2000) {
        out = "Output too long. Please Narrow your Search. If the name is short and you have the full name but not descriptor add ( to the end";
      }
    }

    return out;
  }

  public String fcivarfind(String fciName) {
    List < String > targets = new ArrayList < String > ();
    List < String > outputs = new ArrayList < String > ();

    for (Map.Entry < String, String > entry: FABRIC_INTERMEDIARY.vars.entrySet()) {
      if (entry.getValue().contains(fciName)) {
        String[] splitKeys = entry.getKey().split("\\.");
        String old_clazz = String.join(".", Arrays.copyOfRange(splitKeys, 0, splitKeys.length - 1));
        String clazz = FABRIC_INTERMEDIARY.getClassMappedName(old_clazz);
        String oldDesc = entry.getKey().split(":")[1];
        String desc = FABRIC_INTERMEDIARY.renameClassesInFieldDescriptor(oldDesc);
        targets.add(clazz + "." + entry.getValue() + ":" + desc);
      }

    }

    for (String target: targets) {
      String output = "**FeatureCreep Intermediary:** `" + target + "`\n";

      if (SUGARCANE.reverse.vars.containsKey(target)) {
        String sugarcaneOutput = SUGARCANE.reverse.getVarMappedName(target);
        String oldDesc = ":" + target.split(":")[1];
        String desc = SUGARCANE.reverse.renameClassesInFieldDescriptor(oldDesc);
        output += "**MojMap:** `" + sugarcaneOutput + desc + "`\n";
      }

      if (YARN.reverse.vars.containsKey(target)) {
        String yarnOutput = YARN.reverse.getVarMappedName(target);
        String oldDesc = ":" + target.split(":")[1];
        String desc = YARN.reverse.renameClassesInFieldDescriptor(oldDesc);
        output += "**Yarn:** `" + yarnOutput + desc + "`\n";
      }

      if (SRG.reverse.vars.containsKey(target)) {
        String srgOutput = SRG.reverse.getVarMappedName(target);
        String oldDesc = ":" + target.split(":")[1];
        String desc = SRG.reverse.renameClassesInFieldDescriptor(oldDesc);
        output += "**SRG:** `" + srgOutput + desc + "`\n";
      }

      if (FABRIC_INTERMEDIARY.reverse.vars.containsKey(target)) {
        String fainOutput = FABRIC_INTERMEDIARY.reverse.getVarMappedName(target);
        String oldDesc = ":" + target.split(":")[1];
        String desc = FABRIC_INTERMEDIARY.reverse.renameClassesInFieldDescriptor(oldDesc);
        output += "**Fabric Intermediary:** `" + fainOutput + desc + "`\n";
      }

      outputs.add(output);
    }

    String out;
    if (outputs.isEmpty()) {
      out = "No mapping found for " + fciName + ". Make sure its in the correct format using . instead of / in the main class (but not descriptors) and that the class is actually mapped and not the same on most mappings and that you are on the latest version and that the mapping DB has updated to the newest version.";
    } else {
      out = String.join("\n", outputs);
      if (out.length() > 2000) {
        out = "Output too long. Please Narrow your Search. If the name is short and you have the full name but not descriptor add : to the end";
      }
    }

    return out;
  }

  public String yarnvarfind(String yarn_name) {
    // code here
    boolean pack = yarn_name.contains(".");
    List < String > targets = new ArrayList < String > ();
    List < String > outputs = new ArrayList < String > ();
    if (pack && YARN.vars.containsKey(yarn_name)) {
      targets.add(yarn_name);
    } else {
      for (Map.Entry < String, String > entry: YARN.vars.entrySet()) {
        if (entry.getKey().contains(yarn_name)) {
          targets.add(entry.getKey());
        }
      }
    }

    for (String target: targets) {
      String output = "**Yarn:** `" + target + "`\n";
      if (YARN.vars.containsKey(target)) {
        String fci_output = YARN.getVarMappedName(target);
        String[] old_clazz_split = target.split("\\.");
        String old_clazz = String.join(".", Arrays.copyOfRange(old_clazz_split, 0, old_clazz_split.length - 1));
        String clazz = YARN.getClassMappedName(old_clazz);
        String[] old_desc_split = target.split(":");
        String old_desc = old_desc_split[1];
        String desc = YARN.renameClassesInFieldDescriptor(old_desc);
        fci_output = fci_output + ":" + desc;
        output = output + "**FeatureCreep Intermediary:** `" + fci_output + "`\n";
        fci_output = clazz + "." + fci_output;
        if (SUGARCANE.reverse.vars.containsKey(fci_output)) {
          String sugarcane_output = SUGARCANE.reverse.getVarMappedName(fci_output);
          String[] old_desc_split2 = fci_output.split(":");
          String old_desc2 = old_desc_split2[1];
          String desc2 = SUGARCANE.reverse.renameClassesInFieldDescriptor(old_desc2);
          output = output + "**MojMap:** `" + sugarcane_output + desc2 + "`\n";
        }
        if (SRG.reverse.vars.containsKey(fci_output)) {
          String yarn_output = SRG.reverse.getVarMappedName(fci_output);
          String[] old_desc_split3 = fci_output.split(":");
          String old_desc3 = old_desc_split3[1];
          String desc3 = SRG.reverse.renameClassesInFieldDescriptor(old_desc3);
          output = output + "**SRG:** `" + yarn_output + desc3 + "`\n";
        }
        if (FABRIC_INTERMEDIARY.reverse.vars.containsKey(fci_output)) {
          String fain_output = FABRIC_INTERMEDIARY.reverse.getVarMappedName(fci_output);
          String[] old_desc_split4 = fci_output.split(":");
          String old_desc4 = old_desc_split4[1];
          String desc4 = FABRIC_INTERMEDIARY.reverse.renameClassesInFieldDescriptor(old_desc4);
          output = output + "**Fabric Intermediary:** `" + fain_output + desc4 + "`\n";
        }
      }
      outputs.add(output);
    }

    String out;
    if (outputs.isEmpty()) {
      out = "No mapping found for " + yarn_name + ". Make sure its in the correct format using . instead of / in the main class (but not descriptors) and that the class is actually mapped and not the same on most mappings and that you are on the latest version and that the mapping DB has updated to the newest version.";
    } else {
      out = String.join("\n", outputs);
      if (out.length() > 2000) {
        out = "Output too long. Please Narrow your Search. If the name is short and you have the full name but not descriptor add : to the end";
      }
    }

    return out;
  }

  public String srgvarfind(String srg_name) {
    // code here
    boolean pack = srg_name.contains(".");
    List < String > targets = new ArrayList < String > ();
    List < String > outputs = new ArrayList < String > ();
    if (pack && SRG.vars.containsKey(srg_name)) {
      targets.add(srg_name);
    } else {
      for (Map.Entry < String, String > entry: SRG.vars.entrySet()) {
        if (entry.getKey().contains(srg_name)) {
          targets.add(entry.getKey());
        }
      }
    }

    for (String target: targets) {
      String output = "**SRG:** `" + target + "`\n";
      if (SRG.vars.containsKey(target)) {
        String fci_output = SRG.getVarMappedName(target);
        String[] old_clazz_split = target.split("\\.");
        String old_clazz = String.join(".", Arrays.copyOfRange(old_clazz_split, 0, old_clazz_split.length - 1));
        String clazz = SRG.getClassMappedName(old_clazz);
        String[] old_desc_split = target.split(":");
        String old_desc = old_desc_split[1];
        String desc = SRG.renameClassesInFieldDescriptor(old_desc);
        fci_output = fci_output + ":" + desc;
        output = output + "**FeatureCreep Intermediary:** `" + fci_output + "`\n";
        fci_output = clazz + "." + fci_output;
        if (SUGARCANE.reverse.vars.containsKey(fci_output)) {
          String sugarcane_output = SUGARCANE.reverse.getVarMappedName(fci_output);
          String[] old_desc_split2 = fci_output.split(":");
          String old_desc2 = old_desc_split2[1];
          String desc2 = SUGARCANE.reverse.renameClassesInFieldDescriptor(old_desc2);
          output = output + "**MojMap:** `" + sugarcane_output + ":" + desc2 + "`\n";
        }
        if (YARN.reverse.vars.containsKey(fci_output)) {
          String yarn_output = YARN.reverse.getVarMappedName(fci_output);
          String[] old_desc_split3 = fci_output.split(":");
          String old_desc3 = old_desc_split3[1];
          String desc3 = YARN.reverse.renameClassesInFieldDescriptor(old_desc3);
          output = output + "**Yarn:** `" + yarn_output + ":" + desc3 + "`\n";
        }
        if (FABRIC_INTERMEDIARY.reverse.vars.containsKey(fci_output)) {
          String fain_output = FABRIC_INTERMEDIARY.reverse.getVarMappedName(fci_output);
          String[] old_desc_split4 = fci_output.split(":");
          String old_desc4 = old_desc_split4[1];
          String desc4 = FABRIC_INTERMEDIARY.reverse.renameClassesInFieldDescriptor(old_desc4);
          output = output + "**Fabric Intermediary:** `" + fain_output + ":" + desc4 + "`\n";
        }
      }
      outputs.add(output);
    }

    String out;
    if (outputs.isEmpty()) {
      out = "No mapping found for " + srg_name + ". Make sure its in the correct format using . instead of / in the main class (but not descriptors) and that the class is actually mapped and not the same on most mappings and that you are on the latest version and that the mapping DB has updated to the newest version.";
    } else {
      out = String.join("\n", outputs);
      if (out.length() > 2000) {
        out = "Output too long. Please Narrow your Search. If the name is short and you have the full name but not descriptor add : to the end";
      }
    }

    return out;
  }

  public String sugarcanevarfind(String moj_name) {
    // code here
    boolean pack = moj_name.contains(".");
    List < String > targets = new ArrayList < String > ();
    List < String > outputs = new ArrayList < String > ();
    if (pack && SUGARCANE.vars.containsKey(moj_name)) {
      targets.add(moj_name);
    } else {
      for (Map.Entry < String, String > entry: SUGARCANE.vars.entrySet()) {
        if (entry.getKey().contains(moj_name)) {
          targets.add(entry.getKey());
        }
      }
    }

    for (String target: targets) {
      String output = "**MojMap:** `" + target + "`\n";
      if (SUGARCANE.vars.containsKey(target)) {
        String fci_output = SUGARCANE.getVarMappedName(target);
        String[] old_clazz_split = target.split("\\.");
        String old_clazz = String.join(".", Arrays.copyOfRange(old_clazz_split, 0, old_clazz_split.length - 1));
        String clazz = SUGARCANE.getClassMappedName(old_clazz);
        String[] old_desc_split = target.split(":");
        String old_desc = old_desc_split[1];
        String desc = SUGARCANE.renameClassesInFieldDescriptor(old_desc);
        fci_output = fci_output + ":" + desc;
        output = output + "**FeatureCreep Intermediary:** `" + fci_output + "`\n";
        fci_output = clazz + "." + fci_output;
        if (SRG.reverse.vars.containsKey(fci_output)) {
          String sugarcane_output = SRG.reverse.getVarMappedName(fci_output);
          String[] old_desc_split2 = fci_output.split(":");
          String old_desc2 = old_desc_split2[1];
          String desc2 = SRG.reverse.renameClassesInFieldDescriptor(old_desc2);
          output = output + "**SRG:** `" + sugarcane_output + ":" + desc2 + "`\n";
        }
        if (YARN.reverse.vars.containsKey(fci_output)) {
          String yarn_output = YARN.reverse.getVarMappedName(fci_output);
          String[] old_desc_split3 = fci_output.split(":");
          String old_desc3 = old_desc_split3[1];
          String desc3 = YARN.reverse.renameClassesInFieldDescriptor(old_desc3);
          output = output + "**Yarn:** `" + yarn_output + ":" + desc3 + "`\n";
        }
        if (FABRIC_INTERMEDIARY.reverse.vars.containsKey(fci_output)) {
          String fain_output = FABRIC_INTERMEDIARY.reverse.getVarMappedName(fci_output);
          String[] old_desc_split4 = fci_output.split(":");
          String old_desc4 = old_desc_split4[1];
          String desc4 = FABRIC_INTERMEDIARY.reverse.renameClassesInFieldDescriptor(old_desc4);
          output = output + "**Fabric Intermediary:** `" + fain_output + ":" + desc4 + "`\n";
        }
      }
      outputs.add(output);
    }

    String out;
    if (outputs.isEmpty()) {
      out = "No mapping found for " + moj_name + ". Make sure its in the correct format using . instead of / in the main class (but not descriptors) and that the class is actually mapped and not the same on most mappings and that you are on the latest version and that the mapping DB has updated to the newest version.";
    } else {
      out = String.join("\n", outputs);
      if (out.length() > 2000) {
        out = "Output too long. Please Narrow your Search. If the name is short and you have the full name but not descriptor add : to the end";
      }
    }

    return out;
  }

  public String fainvarfind(String fain_name) {
    // code here
    boolean pack = fain_name.contains(".");
    List < String > targets = new ArrayList < String > ();
    List < String > outputs = new ArrayList < String > ();
    if (pack && FABRIC_INTERMEDIARY.vars.containsKey(fain_name)) {
      targets.add(fain_name);
    } else {
      for (Map.Entry < String, String > entry: FABRIC_INTERMEDIARY.vars.entrySet()) {
        if (entry.getKey().contains(fain_name)) {
          targets.add(entry.getKey());
        }
      }
    }

    for (String target: targets) {
      String output = "**Fabric Intermediary:** `" + target + "`\n";
      if (FABRIC_INTERMEDIARY.vars.containsKey(target)) {
        String fci_output = FABRIC_INTERMEDIARY.getVarMappedName(target);
        String[] old_clazz_split = target.split("\\.");
        String old_clazz = String.join(".", Arrays.copyOfRange(old_clazz_split, 0, old_clazz_split.length - 1));
        String clazz = FABRIC_INTERMEDIARY.getClassMappedName(old_clazz);
        String[] old_desc_split = target.split(":");
        String old_desc = old_desc_split[1];
        String desc = FABRIC_INTERMEDIARY.renameClassesInFieldDescriptor(old_desc);
        fci_output = fci_output + ":" + desc;
        output = output + "**FeatureCreep Intermediary:** `" + fci_output + "`\n";
        fci_output = clazz + "." + fci_output;
        if (SRG.reverse.vars.containsKey(fci_output)) {
          String sugarcane_output = SRG.reverse.getVarMappedName(fci_output);
          String[] old_desc_split2 = fci_output.split(":");
          String old_desc2 = old_desc_split2[1];
          String desc2 = SRG.reverse.renameClassesInFieldDescriptor(old_desc2);
          output = output + "**SRG:** `" + sugarcane_output + ":" + desc2 + "`\n";
        }
        if (YARN.reverse.vars.containsKey(fci_output)) {
          String yarn_output = YARN.reverse.getVarMappedName(fci_output);
          String[] old_desc_split3 = fci_output.split(":");
          String old_desc3 = old_desc_split3[1];
          String desc3 = YARN.reverse.renameClassesInFieldDescriptor(old_desc3);
          output = output + "**Yarn:** `" + yarn_output + ":" + desc3 + "`\n";
        }
        if (SUGARCANE.reverse.vars.containsKey(fci_output)) {
          String fain_output = SUGARCANE.reverse.getVarMappedName(fci_output);
          String[] old_desc_split4 = fci_output.split(":");
          String old_desc4 = old_desc_split4[1];
          String desc4 = SUGARCANE.reverse.renameClassesInFieldDescriptor(old_desc4);
          output = output + "**MojMap:** `" + fain_output + ":" + desc4 + "`\n";
        }
      }
      outputs.add(output);
    }

    String out;
    if (outputs.isEmpty()) {
      out = "No mapping found for " + fain_name + ". Make sure its in the correct format using . instead of / in the main class (but not descriptors) and that the class is actually mapped and not the same on most mappings and that you are on the latest version and that the mapping DB has updated to the newest version.";
    } else {
      out = String.join("\n", outputs);
      if (out.length() > 2000) {
        out = "Output too long. Please Narrow your Search. If the name is short and you have the full name but not descriptor add : to the end";
      }
    }

    return out;
  }

  public String classmap(String input) {
    String[] args = input.split(" ");
    if (args.length == 0) {
      return "Command Usage:\n" +
        "classmap class-name-featurecreep-intermediary\n" + "OR\n" + "classmap -mapping-name classname\n\n" +
        "FeatureCreep Moderation Bot Mapping Module";
    }
    if (args.length == 1) {
      return fciclassfind(args[0]);
    } else if (args.length == 2) {
      String origin = args[0].replace("-", "");
      if (origin.equals("srg")) {
        return srgclassfind(args[1]);
      } else if (origin.equals("fci")) {
        return fciclassfind(args[1]);
      } else if (origin.equals("sugarcane")) {
        return sugarcaneclassfind(args[1]);
      } else if (origin.equals("parchment")) {
        return sugarcaneclassfind(args[1]);
      } else if (origin.equals("moj")) {
        return sugarcaneclassfind(args[1]);
      } else if (origin.equals("yarn")) {
        return yarnclassfind(args[1]);
      } else if (origin.equals("fain")) {
        return fainclassfind(args[1]);
      } else {
        return "Unsupported Origin Mappings. Mapping Options\n" +
          "`srg`  Searge Mappings\n" +
          "`fci` FeatureCreep Intermediary\n" +
          "`sugarcane` SugarCane/MojMap/Parchment\n" +
          "`parchment` SugarCane/MojMap/Parchment\n" +
          "`moj` SugarCane/MojMap/Parchment\n" +
          "`yarn` Yarn\n" +
          "`fain` Fabric Intermediary";
      }
    }
    return null;
  }

  public String defmap(String event) {
    String[] args = event.split(" ");

    if (args.length == 0) {
      return ("Command Usage:\n`defmap class.method(desc)\nOR\n`defmap -mapping-name class.method(desc)\n\nExample: `defmap -fain net.minecraft.class_8143.write(Lnet/minecraft/class_2540;)V\n\nFeatureCreep Moderation Bot Mapping Module");
    } else if (args.length == 1) {
      return (fcideffind(args[0]));
    } else if (args.length == 2) {
      String origin = args[0].replaceAll("-", "");

      if (origin.equals("srg")) {
        return (srgdeffind(args[1]));
      } else if (origin.equals("fci")) {
        return (fcideffind(args[1]));
      } else if (origin.equals("sugarcane") || origin.equals("parchment") || origin.equals("moj")) {
        return (sugarcanedeffind(args[1]));
      } else if (origin.equals("yarn")) {
        return (yarndeffind(args[1]));
      } else if (origin.equals("fain")) {
        return (faindeffind(args[1]));
      } else {
        return ("Unsupported Origin Mappings. Mapping Options\n`srg` Searge Mappings\n`fci` FeatureCreep Intermediary\n`sugarcane` SugarCane/MojMap/Parchment\n`parchment` SugarCane/MojMap/Parchment\n`moj` SugarCane/MojMap/Parchment\n`yarn` Yarn\n`fain` Fabric Intermediary");
      }
    }
    return "Too many Args";
  }

  public String varmap(String event) {
    String[] args = event.split(" ");
    if (args.length == 0) {
      return ("Command Usage:\n`varmap class.method(desc)\nOR\n`varmap -mapping-name class.field:desc\n\nExample: `varmap -srg net.minecraft.src.C_290140_.f_291422_:Ljava/lang/String;\n\nFeatureCreep Moderation Bot Mapping Module\n");
    } else if (args.length == 1) {
      return (fcivarfind(args[0]));
    } else if (args.length == 2) {
      String origin = args[0].replaceAll("-", "");
      if (origin.equals("srg")) {
        return (srgvarfind(args[1]));
      } else if (origin.equals("fci")) {
        return (fcivarfind(args[1]));
      } else if (origin.equals("sugarcane")) {
        return (sugarcanevarfind(args[1]));
      } else if (origin.equals("parchment")) {
        return (sugarcanevarfind(args[1]));
      } else if (origin.equals("moj")) {
        return (sugarcanevarfind(args[1]));
      } else if (origin.equals("yarn")) {
        return (yarnvarfind(args[1]));
      } else if (origin.equals("fain")) {
        return (fainvarfind(args[1]));
      } else {
        return ("Unsupported Origin Mappings. Mapping Options\n`srg` Searge Mappings\n`fci` FeatureCreep Intermediary\n`sugarcane` SugarCane/MojMap/Parchment\n`parchment` SugarCane/MojMap/Parchment\n`moj` SugarCane/MojMap/Parchment\n`yarn` Yarn\n`fain` Fabric Intermediary");
      }
    } else {
      return "Too many Args";
    }

  }

  public String mcp(String event) {
    String[] args = event.split(" ");
    if (args.length == 0) {
      return ("Command Usage:\n`mcp def/var/class\nOR\n`mcp f/m/c def/var/class\n\nExample: `mcp f `f_291422_`\n\nFeatureCreep Moderation Bot Mapping Module");
    } else if (args.length == 1) {
      String item = args[0];
      if (item.startsWith("f_") || item.startsWith("field_") || item.contains(":")) {
        return (srgvarfind(item));
      } else if (item.startsWith("m_") || item.startsWith("func_") || item.contains("(")) {
        return (srgdeffind(item));
      } else {
        return (srgclassfind(args[0]));
      }
    } else if (args.length == 2) {
      String type = args[0].replaceAll("-", "");
      String item = args[1];
      if (type.equals("f") || type.equals("field") || type.equals("var") || type.equals("v")) {
        return (srgvarfind(item));
      } else if (type.equals("m") || type.equals("method") || type.equals("func") || type.equals("def") || type.equals("d")) {
        return (srgdeffind(item));
      } else {
        return (srgclassfind(item));
      }
    } else {
      return "Too many args";
    }
  }

  public String fci(String event) {
    String[] args = event.split(" ");
    if (args.length == 0) {
      return ("Command Usage:\nfci def/var/class\nOR\nfci v/d/c def/var/class\n\nExample: fci v toItem:Lgame/Item;");
    } else if (args.length == 1) {
      String item = args[0];
      if (item.startsWith("var_") || item.contains(":")) {
        return (fcivarfind(item));
      } else if (item.startsWith("def") || item.contains("(")) {
        return (fcideffind(item));
      } else {
        return (fciclassfind(args[0]));
      }
    } else if (args.length == 2) {
      String type = args[0].replaceAll("-", "");
      String item = args[1];
      if (type.equals("f") || type.equals("field") || type.equals("var") || type.equals("v")) {
        return (fcivarfind(item));
      } else if (type.equals("m") || type.equals("method") || type.equals("func") || type.equals("def") || type.equals("d")) {
        return (fcideffind(item));
      } else {
        return (fciclassfind(item));
      }
    } else {
      return "Too many args";
    }
  }

  public String yc(String event) {
    String[] args = event.split(" ");
    return fainclassfind(args[0]);
  }

  public String ym(String event) {
    String[] args = event.split(" ");
    return faindeffind(args[0]);
  }

  public String yf(String event) {
    String[] args = event.split(" ");
    return fainvarfind(args[0]);
  }

  /*	    
		      def qc(event)
    event.respond "https://images-na.ssl-images-amazon.com/images/I/61BZXcXM4ZL._AC_UL1000_.jpg"
  end
  def qm(event)
    event.respond "https://www.youtube.com/watch?v=nkbEzhy1G2o"
  end

  def qf(event)
    event.respond "https://www.youtube.com/watch?v=dQw4w9WgXcQ"
  end
	*/

  public String mappings_main(Message event) {

    String reply = new String("FeatureCreep Moderation Bot Mapping Module." +
      "Commands:" +
      "**mappings** This command. Basic Information about the Mapping Module\n" +
      "**classmap** Searches for Class Mappings\n" +
      "**defmap** Searches for Def/Method/Function Mappings\n" +
      "**varmap** Searches for Variable/Field Mappings\n" +
      "**mcp** Converts srg names to other mappings\n" +
      "**fci** Converts fci names to other mappings\n" +
      "**yc/ym/yf** Converts Fabric Intermediary Classes/Methods/Fields to other mappings\n" +
      "~~**qc/qm/qc** Converts Fabric Intermediary and Hashed MojMap Classes/Methods/Fields to other mappings~~\n");
    return reply;
  }

}
