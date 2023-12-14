/**
 * Author: Grace Bech
 * Date:   19th October 2023.
 * Huffman implementation
 */

import java.io.*;
import java.util.*;


public class HuffmanImplementation implements Huffman{

    /**
     *
     * @param pathName - path to a file to read
     * @return
     * @throws IOException
     */
    @Override
    // Define a class named HuffmanImplementation that implements the Huffman interface.
    public Map<Character, Long> countFrequencies(String pathName) throws IOException {
        Map<Character, Long> frequencyMap = new HashMap<>();
        BufferedReader reader = new BufferedReader(new FileReader(pathName));
// Create a reader to read the file specified by pathName.
        try {
            int c;
            while ((c = reader.read()) != -1) {  // Read characters until the end of the file is reached.
                char character = (char) c; // Convert the integer value to a character.
                if (frequencyMap.containsKey(character)){ // Check if the character is already in the frequency map.
                    frequencyMap.put(character, frequencyMap.get(character) + 1L);  // If yes, increment its frequency.
                }
                else{
                    frequencyMap.put(character, 1L);   // If not, add it with a frequency of 1.
                }

            }

        }
        finally{  // Ensure that the reader is closed after reading, even if an exception occurs.
            reader.close();

        }
        // Return the frequency map.
        return frequencyMap;

    }

    /**
     *
     * @param frequencies a map of Characters with their frequency counts from countFrequencies
     * @return
     */
    @Override
    public BinaryTree<CodeTreeElement> makeCodeTree(Map<Character, Long> frequencies) {
        // If the frequencies map is empty, return null.
        if (frequencies.isEmpty()) return null;
// Priority queue for storing binary trees with frequencies as priority.
        PriorityQueue<BinaryTree<CodeTreeElement>> charPriority = new PriorityQueue<>((bT1, bT2) -> Math.toIntExact(bT1.getData().getFrequency() - bT2.getData().getFrequency())); // need a comparator
        // Create a tree for each character and add it to the priority queue.
        for(Character char1 : frequencies.keySet()){
            CodeTreeElement element = new CodeTreeElement(frequencies.get(char1), char1);
            BinaryTree<CodeTreeElement> myTree = new BinaryTree<>(element);
            charPriority.add(myTree);
        }

        // Build the Huffman tree
        while (charPriority.size() > 1) {
            BinaryTree<CodeTreeElement> tree1 = charPriority.remove(); // Get the first binary Tree and add it into the new Binary Tree1
            BinaryTree<CodeTreeElement> tree2 = charPriority.remove();  // Get the second Binary Tree add to the binary tree2
            Long total1 = tree1.getData().getFrequency() + tree2.getData().getFrequency(); // aDD THE TWO TREES
            CodeTreeElement root = new CodeTreeElement(total1, null);
            BinaryTree<CodeTreeElement> newTree = new BinaryTree<>(root, tree1, tree2);  // Create a new tree which is the sum of the two trees
            charPriority.add(newTree);
        }

        BinaryTree<CodeTreeElement> lastTree = charPriority.remove();

        if(frequencies.size() == 1){  // Special case: if there's only one character, create a new tree with a frequency of 100
            return new BinaryTree<>(new CodeTreeElement(100L, null), lastTree, lastTree);
        }

        return lastTree;

    }

    /**
     *
     * @param codeTree the tree for encoding characters produced by makeCodeTree
     * @return
     */
    @Override
    public Map<Character, String> computeCodes(BinaryTree<CodeTreeElement> codeTree) {
        Map<Character, String> TreeMap = new HashMap<>(); // Create a HashMap to store character codes.
        String myCode = "";    // Initialize an empty string for the current Huffman code.
        computeCodesHelper(TreeMap, myCode, codeTree);       // Call the recursive helper function.
        return TreeMap;    // Return the TreeMap containing character codes.

    }

    /**
     *
     * @param myMap
     * @param myCode
     * @param codeTree
     */
    public void computeCodesHelper(Map<Character, String> myMap, String myCode, BinaryTree<CodeTreeElement> codeTree){
        if(codeTree.isLeaf()){ // If the current node is a leaf, add its character and code to the map.
            myMap.put(codeTree.getData().getChar(), myCode);
        }
        else {   // If not a leaf, recursively traverse the left and right subtrees, updating the code string.
            computeCodesHelper(myMap, myCode + "0", codeTree.getLeft());
            computeCodesHelper(myMap, myCode + "1", codeTree.getRight());
        }

    }

    /**
     *
     * @param codeMap - Map of characters to codes produced by computeCodes
     * @param pathName - File to compress
     * @param compressedPathName - Store the compressed data in this file
     * @throws IOException
     */
    @Override
    // Recursive helper function for computeCodes.
    public void compressFile(Map<Character, String> codeMap, String pathName, String compressedPathName) throws IOException {
            BufferedReader input = new BufferedReader(new FileReader(pathName));  // Create a reader to read the original file.
            BufferedBitWriter output = new BufferedBitWriter(compressedPathName);   // Create a buffered bit writer to write the compressed data.


        try{

                int c = input.read();
                while (c != -1) {    // Read characters until the end of the file is reached.
                    char character = (char) c;
                    String code = codeMap.get(character);
                    for (char bit : code.toCharArray()) {  // Write each bit of the Huffman code to the output.
                        output.writeBit(bit == '1');
                    }

                    c = input.read();
                }
            }
           finally {  // Close both the input and output streams.
               input.close();
               output.close();
           }

    }

    /**
     *
     * @param compressedPathName - file created by compressFile
     * @param decompressedPathName - store the decompressed text in this file, contents should match the original file before compressFile
     * @param codeTree - Tree mapping compressed data to characters
     * @throws IOException
     */
    @Override
    public void decompressFile(String compressedPathName, String decompressedPathName, BinaryTree<CodeTreeElement> codeTree) throws IOException {
            BufferedBitReader input = new BufferedBitReader(compressedPathName);
             BufferedWriter output = new BufferedWriter(new FileWriter(decompressedPathName));

        BinaryTree<CodeTreeElement> currentNode = codeTree;
        try{


            while (input.hasNext()) {
                boolean bit = input.readBit();
                if (bit) {
                    currentNode = currentNode.getRight();
                } else {
                    currentNode = currentNode.getLeft();
                }

                if (currentNode.isLeaf()) {
                    char character = currentNode.getData().getChar();
                    output.write(character);
                    currentNode = codeTree;
                }
            }
        }
        finally {
            input.close();
            output.close();
        }

    }

    /**
     *
     * @param args
     * @throws IOException
     */
    public static void main(String[] args) throws IOException {

        HuffmanImplementation testing = new HuffmanImplementation();
        String OriginalpathName = "/Users/gracebech/IdeaProjects/CS10/CS10/compression/USConstitution.txt";
        String CompressedpathName = "/Users/gracebech/IdeaProjects/CS10/CS10/compression/CompressedUSConstitution.txt";
        String DecompressedpathName = "/Users/gracebech/IdeaProjects/CS10/CS10/compression/DecompressedUSConstitution.txt";
        Map<Character, Long> frequencyMap = null;
        BinaryTree<CodeTreeElement> codeTree = null;
        Map<Character, String> codeMap;
        try {
            frequencyMap = testing.countFrequencies(OriginalpathName);
        } catch (Exception e) {
            System.err.println("Error caught here!" + e.getMessage());
        }


        if (frequencyMap != null) {
            codeTree = testing.makeCodeTree(frequencyMap);
        }


        if (codeTree != null) {
            codeMap = testing.computeCodes(codeTree);
        }

        if (codeTree != null) {
            codeMap = testing.computeCodes(codeTree);


            try {
                testing.compressFile(codeMap, OriginalpathName, CompressedpathName);
            } catch (Exception e) {
                System.err.println("Error caught while compressing" + e.getMessage());
            }
            try {
                testing.decompressFile(CompressedpathName, DecompressedpathName, codeTree);


            } catch (Exception e) {
                System.err.println("Error found while decompressing" + e.getMessage());
            }


        }
    }


}

