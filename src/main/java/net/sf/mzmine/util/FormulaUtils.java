/*
 * Copyright 2006-2018 The MZmine 2 Development Team
 * 
 * This file is part of MZmine 2.
 * 
 * MZmine 2 is free software; you can redistribute it and/or modify it under the terms of the GNU
 * General Public License as published by the Free Software Foundation; either version 2 of the
 * License, or (at your option) any later version.
 * 
 * MZmine 2 is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without
 * even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License along with MZmine 2; if not,
 * write to the Free Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301
 * USA
 */

package net.sf.mzmine.util;

import java.io.IOException;
import java.util.Comparator;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.TreeSet;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.annotation.Nonnull;
import org.openscience.cdk.config.IsotopeFactory;
import org.openscience.cdk.config.Isotopes;
import org.openscience.cdk.interfaces.IChemObjectBuilder;
import org.openscience.cdk.interfaces.IIsotope;
import org.openscience.cdk.interfaces.IMolecularFormula;
import org.openscience.cdk.silent.SilentChemObjectBuilder;
import org.openscience.cdk.tools.manipulator.MolecularFormulaManipulator;
import net.sf.mzmine.datamodel.IonizationType;
import net.sf.mzmine.datamodel.identities.MolecularFormulaIdentity;
import net.sf.mzmine.datamodel.identities.iontype.IonType;

public class FormulaUtils {

  private static Logger logger = Logger.getLogger(FormulaUtils.class.getName());
  private static final double electronMass = 0.00054857990946;

  /**
   * Sort all molecular formulas by score of ppm distance, isotope sccore and msms score (with
   * weighting). Best will be at first position
   * 
   * @param list
   * @param neutralMass
   * @param ppmMax
   * @param weightIsotopeScore
   * @param weightMSMSscore
   */
  public static void sortFormulaList(List<MolecularFormulaIdentity> list, double neutralMass,
      double ppmMax, double weightIsotopeScore, double weightMSMSscore) {
    if (list == null)
      return;
    list.sort(new Comparator<MolecularFormulaIdentity>() {
      @Override
      public int compare(MolecularFormulaIdentity a, MolecularFormulaIdentity b) {
        double scoreA = a.getScore(neutralMass, ppmMax, weightIsotopeScore, weightMSMSscore);
        double scoreB = b.getScore(neutralMass, ppmMax, weightIsotopeScore, weightMSMSscore);
        // best to position 0 (therefore change A B)
        return Double.compare(scoreB, scoreA);
      }
    });
  }

  /**
   * Returns the exact mass of an element. Mass is obtained from the CDK library.
   */
  public static double getElementMass(String element) {
    try {
      Isotopes isotopeFactory = Isotopes.getInstance();
      IIsotope majorIsotope = isotopeFactory.getMajorIsotope(element);
      // If the isotope symbol does not exist, return 0
      if (majorIsotope == null) {
        return 0;
      }
      double mass = majorIsotope.getExactMass();
      return mass;
    } catch (IOException e) {
      e.printStackTrace();
      return 0;
    }
  }

  public static boolean containsElement(IMolecularFormula f, String element) {
    for (IIsotope iso : f.isotopes()) {
      if (iso.getSymbol().equals(element))
        return true;
    }
    return false;
  }

  public static int countElement(IMolecularFormula f, String element) {
    int count = 0;
    for (IIsotope iso : f.isotopes()) {
      if (iso.getSymbol().equals(element))
        count += f.getIsotopeCount(iso);
    }
    return count;
  }

  @Nonnull
  public static Map<String, Integer> parseFormula(String formula) {

    Map<String, Integer> parsedFormula = new Hashtable<String, Integer>();

    Pattern pattern = Pattern.compile("([A-Z][a-z]?)(-?[0-9]*)");
    Matcher matcher = pattern.matcher(formula);

    while (matcher.find()) {
      String element = matcher.group(1);
      String countString = matcher.group(2);
      int addCount = 1;
      if ((countString.length() > 0) && (!countString.equals("-")))
        addCount = Integer.parseInt(countString);
      int currentCount = 0;
      if (parsedFormula.containsKey(element)) {
        currentCount = parsedFormula.get(element);
      }
      int newCount = currentCount + addCount;
      parsedFormula.put(element, newCount);
    }
    return parsedFormula;
  }

  @Nonnull
  public static String formatFormula(@Nonnull Map<String, Integer> parsedFormula) {

    StringBuilder formattedFormula = new StringBuilder();

    // Use TreeSet to sort the elements by alphabet
    TreeSet<String> elements = new TreeSet<String>(parsedFormula.keySet());

    if (elements.contains("C")) {
      int countC = parsedFormula.get("C");
      formattedFormula.append("C");
      if (countC > 1)
        formattedFormula.append(countC);
      elements.remove("C");
      if (elements.contains("H")) {
        int countH = parsedFormula.get("H");
        formattedFormula.append("H");
        if (countH > 1)
          formattedFormula.append(countH);
        elements.remove("H");
      }
    }
    for (String element : elements) {
      formattedFormula.append(element);
      int count = parsedFormula.get(element);
      if (count > 1)
        formattedFormula.append(count);
    }
    return formattedFormula.toString();
  }

  public static double calculateExactMass(String formula) {
    return calculateExactMass(formula, 0);
  }

  /**
   * Calculates exact monoisotopic mass of a given formula. Note that the returned mass may be
   * negative, in case the formula contains negative such as C3H10P-3. This is important for
   * calculating the mass of some ionization adducts, such as deprotonation (H-1).
   */
  public static double calculateExactMass(String formula, int charge) {

    if (formula.trim().length() == 0)
      return 0;

    Map<String, Integer> parsedFormula = parseFormula(formula);

    double totalMass = 0;
    for (String element : parsedFormula.keySet()) {
      int count = parsedFormula.get(element);
      double elementMass = getElementMass(element);
      totalMass += count * elementMass;
    }

    totalMass -= charge * electronMass;

    return totalMass;
  }

  /**
   * Modifies the formula according to the ionization type
   */
  public static String ionizeFormula(String formula, IonType ionType, int charge) {
    StringBuilder combinedFormula = new StringBuilder();
    combinedFormula.append(formula);
    for (int i = 0; i < charge; i++) {
      combinedFormula.append(ionType.getName());
    }

    Map<String, Integer> parsedFormula = parseFormula(combinedFormula.toString());
    return formatFormula(parsedFormula);
  }


  /**
   * Modifies the formula according to the ionization type
   */
  public static String ionizeFormula(String formula, IonizationType ionType, int charge) {

    // No ionization
    if (ionType == IonizationType.NO_IONIZATION)
      return formula;

    StringBuilder combinedFormula = new StringBuilder();
    combinedFormula.append(formula);
    for (int i = 0; i < charge; i++) {
      combinedFormula.append(ionType.getAdduct());
    }

    Map<String, Integer> parsedFormula = parseFormula(combinedFormula.toString());
    return formatFormula(parsedFormula);
  }

  /**
   * Checks if a formula string only contains valid isotopes/elements.
   * 
   * @param formula String of the molecular formula.
   * @return true / false
   */
  public static boolean checkMolecularFormula(String formula) {
    if (formula.matches(".*[äöüÄÖÜß°§$%&/()=?ß²³´`+*~'#;:<>|]")) { // check for this first
      logger.info("Formula contains illegal characters.");
      return false;
    }
    IChemObjectBuilder builder = SilentChemObjectBuilder.getInstance();
    IMolecularFormula molFormula;

    molFormula = MolecularFormulaManipulator.getMajorIsotopeMolecularFormula(formula, builder);

    String simple = MolecularFormulaManipulator.simplifyMolecularFormula(formula);
    String elements[] = simple.split("0");
    String invalid = "";

    boolean found = false;
    for (String element : elements) {
      found = false;
      for (IIsotope iso : molFormula.isotopes()) {
        if (element.equals(iso.getSymbol()) && (iso.getAtomicNumber() != null)
            && (iso.getAtomicNumber() != 0)) {
          // iso.getAtomicNumber() != null has to be checked, e.g. for some reason an element with
          // Symbol "R" and number 0 exists in the CDK
          found = true;
        }
      }
      if (found == false) {
        invalid += element + ", ";
      }
    }

    if (invalid.length() != 0) {
      invalid = invalid.substring(0, invalid.length() - 2);
      logger.warning("Formula invalid! Element(s) " + invalid + " do not exist.");
      return false;
    }
    return true;
  }


  /**
   * Creates a formula with the major isotopes (important to use this method for exact mass
   * calculation over the CDK version, which generates formulas without an exact mass)
   * 
   * @param formula
   * @return the formula or null
   */
  public static IMolecularFormula createMajorIsotopeMolFormula(String formula) {
    try {
      // new formula consists of isotopes without exact mass
      IChemObjectBuilder builder = SilentChemObjectBuilder.getInstance();
      IMolecularFormula f = MolecularFormulaManipulator
          .getMajorIsotopeMolecularFormula(formula.replace(" ", ""), builder);

      if (f == null)
        return null;
      // replace isotopes
      // needed, as MolecularFormulaManipulator method returns isotopes without exact mass info
      try {
        return replaceAllIsotopesWithoutExactMass(f);
      } catch (Exception e) {
        logger.log(Level.SEVERE, "Cannot create formula for: " + formula, e);
        return null;
      }
    } catch (Exception e) {
      logger.log(Level.SEVERE, "Cannot create formula for: " + formula, e);
      return null;
    }

  }

  /**
   * Searches for all isotopes exactmass=null and replaces them with the major isotope
   * 
   * @param f
   * @return
   * @throws IOException
   */
  public static IMolecularFormula replaceAllIsotopesWithoutExactMass(IMolecularFormula f)
      throws IOException {
    for (IIsotope iso : f.isotopes()) {
      // find isotope without exact mass
      if (iso.getExactMass() == null || iso.getExactMass() == 0) {
        int isotopeCount = f.getIsotopeCount(iso);
        f.removeIsotope(iso);

        // replace
        IsotopeFactory iFac = Isotopes.getInstance();
        IIsotope major = iFac.getMajorIsotope(iso.getAtomicNumber());
        if (major != null)
          f.addIsotope(major, isotopeCount);
        return replaceAllIsotopesWithoutExactMass(f);
      }
    }
    // no isotope found
    return f;
  }

  /**
   * 
   * @param result is going to be changed. is also the returned value
   * @param sub
   * @return
   */
  public static IMolecularFormula subtractFormula(IMolecularFormula result, IMolecularFormula sub) {
    for (IIsotope isotope : sub.isotopes()) {
      int count = sub.getIsotopeCount(isotope);
      boolean found = false;
      do {
        found = false;
        for (IIsotope realIsotope : result.isotopes()) {
          // there can be different implementations of IIsotope
          if (equalIsotopes(isotope, realIsotope)) {
            found = true;
            int realCount = result.getIsotopeCount(realIsotope);
            int remaining = realCount - count;
            result.removeIsotope(realIsotope);
            if (remaining > 0)
              result.addIsotope(realIsotope, remaining);
            count -= realCount;
            break;
          }
        }
      } while (count > 0 && found);
    }
    return result;
  }

  /**
   * 
   * @param result is going to be changed. is also the returned value
   * @param add
   * @return
   */
  public static IMolecularFormula addFormula(IMolecularFormula result, IMolecularFormula add) {
    result.add(add);
    return result;
  }


  /**
   * Compare to IIsotope. The method doesn't compare instance but if they have the same symbol,
   * natural abundance and exact mass. TODO
   *
   * @param isotopeOne The first Isotope to compare
   * @param isotopeTwo The second Isotope to compare
   * @return True, if both isotope are the same
   */
  private static boolean equalIsotopes(IIsotope isotopeOne, IIsotope isotopeTwo) {
    if (!isotopeOne.getSymbol().equals(isotopeTwo.getSymbol()))
      return false;
    // exactMass and naturalAbundance is null when using createMajorIsotopeMolFormula
    // // XXX: floating point comparision!
    // if (!Objects.equals(isotopeOne.getNaturalAbundance(), isotopeTwo.getNaturalAbundance()))
    // return false;
    // if (!Objects.equals(isotopeOne.getExactMass(), isotopeTwo.getExactMass()))
    // return false;


    return true;
  }


  public static long getFormulaSize(String formula) {
    long size = 1;

    IChemObjectBuilder builder = SilentChemObjectBuilder.getInstance();
    IMolecularFormula molFormula;

    molFormula = MolecularFormulaManipulator.getMajorIsotopeMolecularFormula(formula, builder);
    Isotopes isotopeFactory;
    try {
      isotopeFactory = Isotopes.getInstance();
      for (IIsotope iso : molFormula.isotopes()) {

        int naturalIsotopes = 0;
        for (IIsotope i : isotopeFactory.getIsotopes(iso.getSymbol())) {
          if (i.getNaturalAbundance() > 0.0) {
            naturalIsotopes++;
          }

        }

        try {
          size = Math.multiplyExact(size, (molFormula.getIsotopeCount(iso) * naturalIsotopes));
        } catch (ArithmeticException e) {
          e.printStackTrace();
          logger.info("Formula size of " + formula + " is too big.");
          return -1;
        }
      }
    } catch (IOException e) {
      logger.warning("Unable to initialise Isotopes.");
      e.printStackTrace();
    }


    return size;
  }
}
