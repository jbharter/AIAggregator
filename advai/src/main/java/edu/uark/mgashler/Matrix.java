package edu.uark.mgashler;
// ----------------------------------------------------------------
// The contents of this file are distributed under the CC0 license.
// See http://creativecommons.org/publicdomain/zero/1.0/
// ----------------------------------------------------------------

import scala.Tuple2;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentSkipListMap;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;


/// This stores a matrix, A.K.A. data set, A.K.A. table. Each element is
/// represented as a double value. Nominal values are represented using their
/// corresponding zero-indexed enumeration value. For convenience,
/// the matrix also stores some meta-data which describes the columns (or attributes)
/// in the matrix.
public class Matrix {

	/// Used to represent elements in the matrix for which the value is not known.
	public static final double UNKNOWN_VALUE = -1e308;

	// Data
    private Map<Integer,Map<Integer,Double>> matrix; //matrix elements row,col
    private Map<Integer,Map<Integer,Tuple2<String,String>>> metadataMatrix;

	// Meta-data
	private String fileName = "";                          // the name of the file
    private int numRows;
    private int numCols;

    // Operators
    private Predicate<Integer> validRow = suspect -> suspect > 0 && suspect < numRows;
    private Predicate<Integer> validCol = suspect -> suspect > 0 && suspect < numCols;
    private Predicate<Tuple2<Integer,Integer>> validPair = suspect -> validRow.test(suspect._1()) && validCol.test(suspect._2());
    private Function<Double,String> defaultFormatter = Object::toString;

    public Consumer<Double> reset = val -> valueMapper(before -> val); /// Sets every element in the matrix to the specified value.
    public Consumer<Double> scale = scalar -> valueMapper(before -> before * scalar); /// Scales every element in the matrix by the specified value.
    public Consumer<Double> shift = scalar -> valueMapper(before -> before + scalar); /// Shifts every element in the matrix by the specified value.

    public Matrix() {
        this.matrix = new ConcurrentSkipListMap<>();
        this.metadataMatrix = new ConcurrentSkipListMap<>();
    }

    private Map<Integer,Double> getRow(int row) {
        return matrix.getOrDefault(row,new ConcurrentHashMap<>());
    }

    private String rowToString(int rowIndex) {
        return rowToString(rowIndex,",");
    }

    private String rowToString(int rowIndex, String delimiter) {
        return rowToString(rowIndex,delimiter,defaultFormatter);
    }

    private String rowToString(int rowIndex, String delimiter, Function<Double,String> valueMapper) {
        return getRow(rowIndex).values().stream().map(valueMapper).collect(Collectors.joining(delimiter));
    }

    public void set(int row, int col, Double val) {
        getRow(row).put(col,val);
    }

    private Double mGet(int row, int col) {
        return getRow(row).getOrDefault(col,UNKNOWN_VALUE);
    }

    public Map<Integer,Map<Integer,Double>> getMatrix() {
        return matrix;
    }

    // Public Methods
    public Matrix setFileName(String fileName) {
        this.fileName = fileName;
        return this;
    }

    public List<String> getAttributeNameList() {
        return metadataMatrix.values()
                .stream()
                .flatMap(each -> each.values().stream())
                .flatMap(tuple2 -> Stream.of(tuple2._1(),tuple2._2()))
                .collect(Collectors.toList());
    }

    public Matrix setAttributeNameList(ArrayList<String> attributeNameList) {
        //this.attributeNameList = attributeNameList;
        return this;
    }

    public ArrayList<HashMap<String, Integer>> getStringToIntegerMapList() {
        return new ArrayList<>();
    }

    public Matrix setStringToIntegerMapList(ArrayList<HashMap<String, Integer>> stringToIntegerMapList) {
        //this.stringToIntegerMapList = stringToIntegerMapList;
        return this;
    }

    public ArrayList<HashMap<Integer, String>> getIntegerToStringMapList() {
        return new ArrayList<>();
    }

    public Matrix setIntegerToStringMapList(ArrayList<HashMap<Integer, String>> integerToStringMapList) {
        //this.integerToStringMapList = integerToStringMapList;
        return this;
    }

    public String toString() {
        return IntStream.range(0,numRows).mapToObj(this::rowToString).collect(Collectors.joining("\n"));
	}

    public String getFileName() {
        return fileName;
    }

    public boolean validRowCol(int row, int col) {
        return validRow.test(row) && validCol.test(col);
    }

	public Double get(int row, int col) {
        if (validRowCol(row,col)) {
            return getRow(row).get(col);
        } else {
            return null;
        }
    }

    public Collection<Map<Integer,Double>> getRows() {
        return matrix.values();
    }

    public Map<Integer,Double> row(int index) {
        return getRow(index);
    }

	public Map<Integer,Double> newRow() { /// Adds one new row to this matrix. Returns a reference to the new row.
        int rowIndex = matrixHeight() + 1;
        ++numRows;

		return matrix.computeIfAbsent(rowIndex,idx -> {
		    Map<Integer, Double> newrow = new ConcurrentHashMap<>();
		    IntStream.range(0,numCols).forEach(col -> newrow.put(col,UNKNOWN_VALUE));
		    return newrow;
        });
	}

    public int matrixHeight() {
        return matrix.keySet().stream().mapToInt(Integer.class::cast).max().orElse(-1);
    }

    public int numRows() { /// Returns the number of numRows in the matrix
        return numRows;
    }

    public int numCols() { /// Returns the number of columns (or attributes) in the matrix
        return numCols;
    }

    public void valueMapper(Function<Double,Double> mapper) {
        for (Map.Entry<Integer,Map<Integer,Double>> mEntry : matrix.entrySet()) {
            int row = mEntry.getKey();
            mEntry.getValue().keySet().forEach(idx -> {
                Double val = mapper.apply(mGet(row,idx));
                set(row,idx,val);
            });
        }
    }

    public void addScaled(Matrix that, double scalar) { /// Adds every element in that matrix to this one
        if(that.numRows() != this.numRows() || that.numCols() != this.numCols()) {
            throw new IllegalArgumentException("Mismatching size");
        }
        for (int i = 0; i < numRows(); i++) {
            for(int j = 0; j < numRows; j++) {
                Double val = get(i,j) + (scalar * that.mGet(i,j));
                set(i,j,val);
            }
        }
    }

    public void newRows(int n) { /// Adds 'n' new numRows to this matrix
        IntStream.range(0,n).forEach(v -> newRow());
    }

    public void newColumn(Number val) { /// Adds a column to this matrix with the specified number of values. (Use 0 for a continuous attribute.)

        getRows().forEach(row -> row.put(numCols,val.doubleValue()));

        // Bump Col val
        numCols++;
    }

    public void newColumn() { /// Adds a column to this matrix with 0 values (continuous data).
        this.newColumn(0);
    }

    public void newColumns(int n) { /// Adds n columns to this matrix, each with 0 values (continuous data).
        IntStream.range(0,n).forEach(integer -> newColumn());
    }

    public void setSize(int rows, int cols) {
        /// Makes a numRows-by-columns matrix of *ALL CONTINUOUS VALUES*.
        /// This method wipes out any data currently in the matrix. It also
        /// wipes out any meta-data.

        matrix.clear();
        numRows = rows;
        numCols = cols;

        // Set the meta-data
        setFileName("");
        getAttributeNameList().clear();
        getStringToIntegerMapList().clear();
        getIntegerToStringMapList().clear();

        // Make space for each of the columns, then each of the numRows
        newColumns(cols);
        newRows(rows);
    }

    //	public Matrix(int rows, int cols) {
//		setSize(rows, cols);
//	}

//	public Matrix(Matrix that) {
//	    setFileName(that.getFileName());
//		setSize(that.numRows(), that.numCols());
//		copyBlock(0, 0, that, 0, 0, that.numRows(), that.numCols()); // (copies the meta data too)
//	}


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
    /// Clears this matrix and copies the meta-data from that matrix.
    /// In other words, it makes a zero-row matrix with the same number
    /// of columns as "that" matrix. You will need to call newRow or newRows
    /// to give the matrix some numRows.
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

	/// Adds one new row to this matrix at the specified location. Returns a reference to the new row.
//	public double[] insertRow(int i) {
//		int c = numCols();
//		if (c == 0)
//			throw new IllegalArgumentException("You must add some columns before you add any numRows.");
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
//		if(row.length != numCols())
//			throw new IllegalArgumentException("Row size differs from the number of columns in this matrix.");
//		matrixElements.add(row);
//	}


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



	/// Swaps the positions of the two specified numRows
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
//		setSize(that.numRows(), that.numCols());
//		copyBlock(0, 0, that, 0, 0, that.numRows(), that.numCols());
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
//	public void copyBlock(int destRow, int destCol, Matrix that, int rowBegin, int colBegin, int rowCount, int colCount) {
//		if (destRow + rowCount > this.numRows() || destCol + colCount > this.numCols()) {
//		    throw new IllegalArgumentException("Out of range for destination matrix.");
//        }
//		if (rowBegin + rowCount > that.numRows() || colBegin + colCount > that.numCols()) {
//		    throw new IllegalArgumentException("Out of range for source matrix.");
//        }
//
//		// Copy the specified region of meta-data
//		for (int i = 0; i < colCount; i++) {
//			attributeNameList.set(destCol + i, that.attributeNameList.get(colBegin + i));
//			stringToIntegerMapList.set(destCol + i, new HashMap<>(that.stringToIntegerMapList.get(colBegin + i)));
//			integerToStringMapList.set(destCol + i, new HashMap<>(that.integerToStringMapList.get(colBegin + i)));
//		}
//
//		// Copy the specified region of data
//		for (int i = 0; i < rowCount; i++) {
//			List<Double> source = that.row(rowBegin + i);
//			List<Double> dest = this.row(destRow + i);
//			for(int j = 0; j < colCount; j++) {
//			    dest.set(destCol + j, source.get(colBegin + j));
//            }
//		}
//	}




	/// Sets this to the identity matrix.
//	public void setToIdentity() {
//		setAll(0.0);
//		int m = Math.min(numCols(), numRows());
//		for(int i = 0; i < m; i++) {
//		    row(i)[i] = 1.0;
//        }
//	}


	/// Throws an exception if that has a different number of columns than
	/// this, or if one of its columns has a different number of values.
//	public void checkCompatibility(Matrix that) {
//		int c = numCols();
//		if (that.numCols() != c) {
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
    //	public void printRow(double[] row, PrintStream os) {
//	    printRow(row, new PrintWriter(os));
//	}


//	public void printRow(double[] row, PrintWriter os) {
//		if(row.length != numCols()) {
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
//			if (j + 1 < numCols()) {
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
//			for (int i = 0; i < numRows(); i++) {
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
    //    public Json marshal() {
//		Json list = Json.newList();
//		for(int i = 0; i < numRows(); i++) {
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
}
