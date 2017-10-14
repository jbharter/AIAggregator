package edu.uark.mgashler;
// ----------------------------------------------------------------
// The contents of this file are distributed under the CC0 license.
// See http://creativecommons.org/publicdomain/zero/1.0/
// ----------------------------------------------------------------

import java.util.Arrays;
import java.util.HashMap;
import java.util.ArrayList;
import java.lang.StringBuilder;
import java.util.List;
import java.util.stream.Collectors;


/// This stores a matrix, A.K.A. data set, A.K.A. table. Each element is
/// represented as a double value. Nominal values are represented using their
/// corresponding zero-indexed enumeration value. For convenience,
/// the matrix also stores some meta-data which describes the columns (or attributes)
/// in the matrix.
public class Matrix {
	/// Used to represent elements in the matrix for which the value is not known.
	public static final double UNKNOWN_VALUE = -1e308; 

	// Data
	private List<List<Double>> matrixElements = new ArrayList<>(); //matrix elements

	// Meta-data
	private String fileName = "";                          // the name of the file
	private ArrayList<String> attributeNameList = new ArrayList<>();                 // the name of each attribute (or column)
	private ArrayList<HashMap<String, Integer>> stringToIntegerMapList = new ArrayList<>(); // value to enumeration
	private ArrayList<HashMap<Integer, String>> integerToStringMapList = new ArrayList<>(); // enumeration to value

    public Matrix(){}
	public Matrix(int rows, int cols) {
		setSize(rows, cols);
	}

	public Matrix(Matrix that) {
	    setFileName(that.getFileName());
		setSize(that.rows(), that.cols());
		copyBlock(0, 0, that, 0, 0, that.rows(), that.cols()); // (copies the meta data too)
	}


//	public Matrix(Json n) {
//		int rowCount = n.size();
//		int colCount = n.get(0).size();
//
//		setSize(rowCount, colCount);
//		for(int i = 0; i < rowCount; i++) {
//			double[] mrow = row(i);
//			Json jrow = n.get(i);
//			for(int j = 0; j < colCount; j++) {
//				mrow[j] = jrow.getDouble(j);
//			}
//		}
//	}

    public String getFileName() {
        return fileName;
    }

    public Matrix setFileName(String fileName) {
        this.fileName = fileName;
        return this;
    }

    public ArrayList<String> getAttributeNameList() {
        return attributeNameList;
    }

    public Matrix setAttributeNameList(ArrayList<String> attributeNameList) {
        this.attributeNameList = attributeNameList;
        return this;
    }

    public ArrayList<HashMap<String, Integer>> getStringToIntegerMapList() {
        return stringToIntegerMapList;
    }

    public Matrix setStringToIntegerMapList(ArrayList<HashMap<String, Integer>> stringToIntegerMapList) {
        this.stringToIntegerMapList = stringToIntegerMapList;
        return this;
    }

    public ArrayList<HashMap<Integer, String>> getIntegerToStringMapList() {
        return integerToStringMapList;
    }

    public Matrix setIntegerToStringMapList(ArrayList<HashMap<Integer, String>> integerToStringMapList) {
        this.integerToStringMapList = integerToStringMapList;
        return this;
    }

//    public Json marshal() {
//		Json list = Json.newList();
//		for(int i = 0; i < rows(); i++) {
//		    list.add(Vec.marshal(row(i)));
//        }
//		return list;
//	}

	/// Loads the matrix from an ARFF file
//	public void loadARFF(String filename) {
//		int attrCount = 0; // Count number of attributes
//		int lineNum = 0; // Used for exception messages
//		Scanner s = null;
//		stringToIntegerMapList.clear();
//		integerToStringMapList.clear();
//		attributeNameList.clear();
//
//		try {
//			s = new Scanner(new File(filename));
//			while (s.hasNextLine()) {
//				lineNum++;
//				String line  = s.nextLine().trim();
//				String upper = line.toUpperCase();
//
//				if (upper.startsWith("@RELATION")) {
//				    fileName = line.split(" ")[1];
//                } else if (upper.startsWith("@ATTRIBUTE")) {
//					HashMap<String, Integer> str_to_enum = new HashMap<>();
//					HashMap<Integer, String> enum_to_str = new HashMap<>();
//					stringToIntegerMapList.add(str_to_enum);
//					integerToStringMapList.add(enum_to_str);
//
//					Json.StringParser sp = new Json.StringParser(line);
//					sp.advance(10);
//					sp.skipWhitespace();
//					String attrName = sp.untilWhitespace();
//					attributeNameList.add(attrName);
//					sp.skipWhitespace();
//					int valCount = 0;
//					if(sp.peek() == '{') {
//						sp.advance(1);
//						while(sp.peek() != '}') {
//							sp.skipWhitespace();
//							String attrVal = sp.untilQuoteSensitive(',', '}');
//							if(sp.peek() == ',')
//								sp.advance(1);
//							if(str_to_enum.containsKey(attrVal))
//								throw new RuntimeException("Duplicate attribute value: " + attrVal);
//							str_to_enum.put(attrVal, valCount);
//							enum_to_str.put(valCount, attrVal);
//							valCount++;
//						}
//						sp.advance(1);
//					}
//					attrCount++;
//				} else if (upper.startsWith("@DATA")) {
//					matrixElements.clear();
//
//					while (s.hasNextLine()) {
//						lineNum++;
//						line = s.nextLine().trim();
//						if (line.startsWith("%") || line.isEmpty()) {
//						    continue;
//                        }
//						double[] row = new double[attrCount];
//						matrixElements.add(row);
//						Json.StringParser sp = new Json.StringParser(line);
//
//						for(int i = 0; i < attrCount; i++) {
//							sp.skipWhitespace();
//							String val = sp.untilQuoteSensitive(',', '\n');
//
//							int valueCount = integerToStringMapList.get(i).size();
//							if (val.equals("?")) { // Unknown values are always set to UNKNOWN_VALUE
//								row[i] = UNKNOWN_VALUE;
//							} else if (valueCount > 0) { // if it's nominal
//
//								HashMap<String, Integer> enumMap = stringToIntegerMapList.get(i);
//								if (!enumMap.containsKey(val)) {
//									throw new IllegalArgumentException("Unrecognized enumeration value " + val + " on line: " + lineNum + ".");
//								}
//
//								row[i] = (double)enumMap.get(val);
//							} else {// else it's continuous
//                                row[i] = Double.parseDouble(val); // The attribute is continuous
//                            }
//							sp.advance(1);
//						}
//					}
//				}
//			}
//		} catch (FileNotFoundException e) {
//			throw new IllegalArgumentException("Failed to open file: " + filename + ".");
//		} finally {
//            if (s != null) {
//                s.close();
//            }
//        }
//	}

	public String toString() {
		StringBuilder sb = new StringBuilder();
		for(int j = 0; j < rows(); j++) {
			if(j > 0) {
			    sb.append("\n");
            }
            String s = row(j).stream().map(Object::toString).collect(Collectors.joining(","));
			sb.append(s);
		}
		return sb.toString();
	}


//	public void printRow(double[] row, PrintStream os) {
//	    printRow(row, new PrintWriter(os));
//	}


//	public void printRow(double[] row, PrintWriter os) {
//		if(row.length != cols()) {
//		    throw new RuntimeException("Unexpected row size");
//        }
//		for (int j = 0; j < row.length; j++) {
//			if (row[j] == UNKNOWN_VALUE) {
//			    os.print("?");
//            } else {
//				int vals = valueCount(j);
//				if (vals == 0) {
//					if(Math.floor(row[j]) == row[j]) {
//					    os.print((int)Math.floor(row[j]));
//                    } else {
//					    os.print(row[j]);
//                    }
//				} else {
//					int val = (int)row[j];
//					if (val >= vals) {
//					    throw new IllegalArgumentException("Value out of range.");
//                    }
//					os.print(attrValue(j, val));
//				}
//			}
//
//			if (j + 1 < cols()) {
//			    os.print(",");
//            }
//		}
//	}


	/// Saves the matrix to an ARFF file
//	public void saveARFF(String filename) {
//		PrintWriter os = null;
//
//		try {
//			os = new PrintWriter(filename);
//			// Print the relation name, if one has been provided ('x' is default)
//			os.print("@RELATION ");
//			os.println(fileName.isEmpty() ? "x" : fileName);
//
//			// Print each attribute in order
//			for (int i = 0; i < attributeNameList.size(); i++) {
//				os.print("@ATTRIBUTE ");
//
//				String attributeName = this.attributeNameList.get(i);
//				os.print(attributeName.isEmpty() ? "x" : attributeName);
//
//				int vals = valueCount(i);
//
//				if (vals == 0) {
//				    os.println(" REAL");
//                }
//				else {
//					os.print(" {");
//					for (int j = 0; j < vals; j++)
//					{
//						os.print(attrValue(i, j));
//						if (j + 1 < vals) os.print(",");
//					}
//					os.println("}");
//				}
//			}
//
//			// Print the data
//			os.println("@DATA");
//			for (int i = 0; i < rows(); i++) {
//				double[] row = matrixElements.get(i);
//				printRow(row, os);
//				os.println();
//			}
//		} catch (FileNotFoundException e) {
//			throw new IllegalArgumentException("Error creating file: " + filename + ".");
//		} finally {
//			os.close();
//		}
//	}

	/// Makes a rows-by-columns matrix of *ALL CONTINUOUS VALUES*.
	/// This method wipes out any data currently in the matrix. It also
	/// wipes out any meta-data.
	public void setSize(int rows, int cols) {
		matrixElements.clear();

		// Set the meta-data
		setFileName("");
		getAttributeNameList().clear();
		getStringToIntegerMapList().clear();
		getIntegerToStringMapList().clear();

		// Make space for each of the columns, then each of the rows
		newColumns(cols);
		newRows(rows);
	}

	/// Clears this matrix and copies the meta-data from that matrix.
	/// In other words, it makes a zero-row matrix with the same number
	/// of columns as "that" matrix. You will need to call newRow or newRows
	/// to give the matrix some rows.
//	public void copyMetaData(Matrix that) {
//		matrixElements.clear();
//		attributeNameList = new ArrayList<>(that.attributeNameList);
//
//		// Make a deep copy of that.stringToIntegerMapList
//		stringToIntegerMapList = new ArrayList<>();
//		for (HashMap<String, Integer> map : that.stringToIntegerMapList) {
//			HashMap<String, Integer> temp = new HashMap<>();
//			for (Map.Entry<String, Integer> entry : map.entrySet()) {
//			    temp.put(entry.getKey(), entry.getValue());
//            }
//			stringToIntegerMapList.add(temp);
//		}
//
//		// Make a deep copy of that.m_enum_to_string
//		integerToStringMapList = new ArrayList<>();
//		for (HashMap<Integer, String> map : that.integerToStringMapList) {
//			HashMap<Integer, String> temp = new HashMap<>();
//			for (Map.Entry<Integer, String> entry : map.entrySet()) {
//			    temp.put(entry.getKey(), entry.getValue());
//            }
//			integerToStringMapList.add(temp);
//		}
//	}


	/// Adds a column with the specified name
//	public void newColumn(String name) {
//		matrixElements.clear();
//		attributeNameList.add(name);
//		stringToIntegerMapList.add(new HashMap<>());
//		integerToStringMapList.add(new HashMap<>());
//	}


	/// Adds a column to this matrix with the specified number of values. (Use 0 for
	/// a continuous attribute.) This method also sets the number of rows to 0, so
	/// you will need to call newRow or newRows when you are done adding columns.
	public void newColumn(int vals) {
		matrixElements.clear();
		String name = "col_" + cols();
		
		attributeNameList.add(name);
		
		HashMap<String, Integer> temp_str_to_enum = new HashMap<>();
		HashMap<Integer, String> temp_enum_to_str = new HashMap<>();
		
		for (int i = 0; i < vals; i++) {
			String sVal = "val_" + i;
			temp_str_to_enum.put(sVal, i);
			temp_enum_to_str.put(i, sVal);
		}
		
		stringToIntegerMapList.add(temp_str_to_enum);
		integerToStringMapList.add(temp_enum_to_str);
	}
	

	/// Adds a column to this matrix with 0 values (continuous data).
	public void newColumn() {
		this.newColumn(0);
	}
	

	/// Adds n columns to this matrix, each with 0 values (continuous data).
	public void newColumns(int n) {
		for (int i = 0; i < n; i++) {
		    newColumn();
        }
	}


	/// Returns the index of the specified value in the specified column.
	/// If there is no such value, adds it to the column.
//	public int findOrCreateValue(int column, String val) {
//		Integer i = stringToIntegerMapList.get(column).get(val);
//		if(i == null) {
//			int nextVal = integerToStringMapList.get(column).size();
//			Integer integ = nextVal;
//			integerToStringMapList.get(column).put(integ, val);
//			stringToIntegerMapList.get(column).put(val, integ);
//			return nextVal;
//		} else {
//            return i;
//        }
//	}


	/// Adds one new row to this matrix. Returns a reference to the new row.
	public List<Double> newRow() {
		int c = cols();
		if (c == 0) {
		    throw new IllegalArgumentException("You must add some columns before you add any rows.");
        }
		List<Double> newRow = new ArrayList<>(c);
		matrixElements.add(newRow);
		return newRow;
	}


	/// Adds one new row to this matrix at the specified location. Returns a reference to the new row.
//	public double[] insertRow(int i) {
//		int c = cols();
//		if (c == 0)
//			throw new IllegalArgumentException("You must add some columns before you add any rows.");
//		double[] newRow = new double[c];
//		matrixElements.add(i, newRow);
//		return newRow;
//	}


	/// Removes the specified row from this matrix. Returns a reference to the removed row.
//	public double[] removeRow(int i)
//	{
//		return matrixElements.remove(i);
//	}


	/// Appends the specified row to this matrix.
//	public void takeRow(double[] row) {
//		if(row.length != cols())
//			throw new IllegalArgumentException("Row size differs from the number of columns in this matrix.");
//		matrixElements.add(row);
//	}


	/// Adds 'n' new rows to this matrix
	public void newRows(int n) {
		for (int i = 0; i < n; i++) {
		    newRow();
        }
	}


	/// Returns the number of rows in the matrix
	public int rows() {
	    return matrixElements.size();
	}


	/// Returns the number of columns (or attributes) in the matrix
	public int cols() {
	    return attributeNameList.size();
	}


	/// Returns the name of the specified attribute
//	public String attrName(int col) {
//	    return attributeNameList.get(col);
//	}


	/// Returns the name of the specified value
//	public String attrValue(int attr, int val) {
//		String value = integerToStringMapList.get(attr).get(val);
//		if (value == null) {
//		    throw new IllegalArgumentException("No name");
//        } else {
//		    return value;
//        }
//	}


//	public String getString(int r, int c) {
//		double val = row(r)[c];
//		return attrValue(c, (int)val);
//	}

	/// Returns the enumerated index of the specified string
//	public int valueEnum(int attr, String val) {
//		Integer i = stringToIntegerMapList.get(attr).get(val);
//		if (i == null) {
//			// Make a very detailed error message listing all possible choices
//			StringBuilder s = new StringBuilder();
//            for (Map.Entry<String, Integer> stringIntegerEntry : stringToIntegerMapList.get(attr).entrySet()) {
//                if (s.length() > 0) {
//                    s.append(", ");
//                }
//                s.append("\"").append(stringIntegerEntry.getKey()).append("\"");
//                s.append("->");
//                s.append(Integer.toString(stringIntegerEntry.getValue()));
//            }
//			throw new IllegalArgumentException("No such value: \"" + val + "\". Choices are: " + s);
//		} else {
//		    return i;
//        }
//	}


	/// Returns a reference to the specified row
	public List<Double> row(int index) {
	    return matrixElements.get(index);
	}


	/// Swaps the positions of the two specified rows
//	public void swapRows(int a, int b) {
//		double[] temp = matrixElements.get(a);
//		matrixElements.set(a, matrixElements.get(b));
//		matrixElements.set(b, temp);
//	}


	/// Returns the number of values associated with the specified attribute (or column)
	/// 0 = continuous, 2 = binary, 3 = trinary, etc.
//	public int valueCount(int attr) {
//	    return integerToStringMapList.get(attr).size();
//	}


	/// Copies that matrix
//	void copy(Matrix that) {
//		setSize(that.rows(), that.cols());
//		copyBlock(0, 0, that, 0, 0, that.rows(), that.cols());
//	}


	/// Returns the mean of the elements in the specified column. (Elements with the value UNKNOWN_VALUE are ignored.)
//	public double columnMean(int col) {
//		double sum = 0.0;
//		int count = 0;
//		for (double[] list : matrixElements) {
//			double val = list[col];
//			if (val != UNKNOWN_VALUE) {
//				sum += val;
//				count++;
//			}
//		}
//
//		return sum / count;
//	}


	/// Returns the minimum element in the specified column. (Elements with the value UNKNOWN_VALUE are ignored.)
//	public double columnMin(int col) {
//		double min = Double.MAX_VALUE;
//		for (double[] list : matrixElements) {
//			double val = list[col];
//			if (val != UNKNOWN_VALUE) {
//			    min = Math.min(min, val);
//            }
//		}
//
//		return min;
//	}


	/// Returns the maximum element in the specifed column. (Elements with the value UNKNOWN_VALUE are ignored.)
//	public double columnMax(int col) {
//		double max = -Double.MAX_VALUE;
//		for (double[] list : matrixElements) {
//			double val = list[col];
//			if (val != UNKNOWN_VALUE) {
//			    max = Math.max(max, val);
//            }
//		}
//
//		return max;
//	}


	/// Returns the most common value in the specified column. (Elements with the value UNKNOWN_VALUE are ignored.)
//	public double mostCommonValue(int col) {
//		HashMap<Double, Integer> counts = new HashMap<>();
//		for (double[] list : matrixElements) {
//			double val = list[col];
//			if (val != UNKNOWN_VALUE) {
//				Integer result = counts.get(val);
//				if (result == null) {
//				    result = 0;
//                }
//
//				counts.put(val, result + 1);
//			}
//		}
//
//		int valueCount = 0;
//		double value   = 0;
//		for (Map.Entry<Double, Integer> entry : counts.entrySet()) {
//			if (entry.getValue() > valueCount) {
//				value      = entry.getKey();
//				valueCount = entry.getValue();
//			}
//		}
//		return value;
//	}


	/// Copies the specified rectangular portion of that matrix, and puts it in the specified location in this matrix.
	public void copyBlock(int destRow, int destCol, Matrix that, int rowBegin, int colBegin, int rowCount, int colCount) {
		if (destRow + rowCount > this.rows() || destCol + colCount > this.cols()) {
		    throw new IllegalArgumentException("Out of range for destination matrix.");
        }
		if (rowBegin + rowCount > that.rows() || colBegin + colCount > that.cols()) {
		    throw new IllegalArgumentException("Out of range for source matrix.");
        }

		// Copy the specified region of meta-data
		for (int i = 0; i < colCount; i++) {
			attributeNameList.set(destCol + i, that.attributeNameList.get(colBegin + i));
			stringToIntegerMapList.set(destCol + i, new HashMap<>(that.stringToIntegerMapList.get(colBegin + i)));
			integerToStringMapList.set(destCol + i, new HashMap<>(that.integerToStringMapList.get(colBegin + i)));
		}

		// Copy the specified region of data
		for (int i = 0; i < rowCount; i++) {
			List<Double> source = that.row(rowBegin + i);
			List<Double> dest = this.row(destRow + i);
			for(int j = 0; j < colCount; j++) {
			    dest.set(destCol + j, source.get(colBegin + j));
            }
		}
	}


	/// Sets every element in the matrix to the specified value.
	public void setAll(double val) {
		for (List<Double> vec : matrixElements) {
			for(int i = 0; i < vec.size(); i++) {
			    vec.set(i,val);
            }
		}
	}


	/// Sets every element in the matrix to the specified value.
	public void scale(double scalar) {
		for (List<Double> vec : matrixElements) {
			for(int i = 0; i < vec.size(); i++) {
			    vec.set(i,vec.get(i)*scalar);
            }
		}
	}


	/// Adds every element in that matrix to this one
	public void addScaled(Matrix that, double scalar) {
		if(that.rows() != this.rows() || that.cols() != this.cols()) {
		    throw new IllegalArgumentException("Mismatching size");
        }
		for (int i = 0; i < rows(); i++) {
		    List<Double> dest = this.row(i);
            List<Double> src = that.row(i);

            for(int j = 0; j < dest.size(); j++) {
                dest.set(j,dest.get(j)+(scalar * src.get(j)));
            }
		}
	}


	/// Sets this to the identity matrix.
//	public void setToIdentity() {
//		setAll(0.0);
//		int m = Math.min(cols(), rows());
//		for(int i = 0; i < m; i++) {
//		    row(i)[i] = 1.0;
//        }
//	}


	/// Throws an exception if that has a different number of columns than
	/// this, or if one of its columns has a different number of values.
//	public void checkCompatibility(Matrix that) {
//		int c = cols();
//		if (that.cols() != c) {
//		    throw new IllegalArgumentException("Matrices have different number of columns.");
//        }
//
//		for (int i = 0; i < c; i++) {
//			if (valueCount(i) != that.valueCount(i)) {
//			    throw new IllegalArgumentException("Column " + i + " has mis-matching number of values.");
//            }
//		}
//	}

//	private static class SortComparator implements Comparator<double[]> {
//		final int column;
//		final boolean ascending;
//
//		SortComparator(int column, boolean ascending) {
//			this.column = column;
//			this.ascending = ascending;
//		}
//
//		public int compare(double[] a, double[] b) {
//			if(ascending) {
//				if(a[column] < b[column]) {
//				    return -1;
//                } else if(a[column] > b[column]) {
//				    return 1;
//                } else {
//				    return 0;
//                }
//			} else {
//				if(a[column] < b[column]) {
//				    return 1;
//                } else if(a[column] > b[column]) {
//				    return -1;
//                } else {
//				    return 0;
//                }
//			}
//		}
//	}

//	public void sort(int column, boolean ascending) {
//		matrixElements.sort(new SortComparator(column, ascending));
//	}
}
