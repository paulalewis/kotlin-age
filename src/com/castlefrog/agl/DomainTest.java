package com.castlefrog.agl;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.commons.math3.stat.descriptive.moment.Mean;
import org.apache.commons.math3.stat.descriptive.moment.StandardDeviation;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import org.xml.sax.SAXException;

import com.castlefrog.agl.Agent;
import com.castlefrog.agl.Agents;
import com.castlefrog.agl.Arbiter;
import com.castlefrog.agl.Simulator;
import com.castlefrog.agl.TurnType;
import com.castlefrog.agl.domains.backgammon.BackgammonSimulator;
import com.castlefrog.agl.domains.biniax.BiniaxSimulator;
import com.castlefrog.agl.domains.havannah.HavannahSimulator;
import com.castlefrog.agl.domains.hex.HexSimulator;
import com.castlefrog.agl.domains.mathax.MathaxSimulator;

/**
 * This class is used to run tests between agents on simulators.
 */
public class DomainTest {
    public static final String TEST_ROOT_NAME = "domainTest";
    /**
     * @param args
     *      test_filepath - path to the xml test file
     *      output_filepath - optional path to output file
     */
    public DomainTest(String[] args) {
        if (args.length == 1 || args.length == 2) {
            registerAgents();
            try {
                List<Agent> agents = new ArrayList<Agent>();
                File xmlFile = new File(args[0]);
                DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
                DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
                Document doc = dBuilder.parse(xmlFile);
                doc.getDocumentElement().normalize();

                if (!doc.getDocumentElement().getNodeName().equals(TEST_ROOT_NAME))
                    throw new IllegalArgumentException("Root tag should be '" + TEST_ROOT_NAME + "'");
                int nSimulations = Integer.parseInt(getTagValue("nSimulations",doc.getDocumentElement()));
                NodeList nList = doc.getElementsByTagName("world");
                Element element = (Element) nList.item(0);
                String simulatorName = getTagValue("name", element);
                Simulator<?, ?> world = selectSimulator(simulatorName, null);
                Simulator<?, ?> simulatedWorld = selectSimulator(simulatorName, null);

                NodeList agentList = doc.getElementsByTagName("agents");
                NodeList agentNodes = ((Element)agentList.item(0)).getElementsByTagName("agent");
                for (int i = 0; i < agentNodes.getLength(); i += 1) {
                    Node agentNode = agentNodes.item(i);
                    if (agentNode.getNodeType() == Node.ELEMENT_NODE) {
                        String name = getTagValue("name", (Element) agentNode);
                        NodeList paramList = doc.getElementsByTagName("params");
                        String[] params = null;
                        if (paramList.getLength() != 0) {
                            NodeList paramNodes = ((Element)paramList.item(0)).getElementsByTagName("param");
                            params = new String[paramNodes.getLength()];
                            for (int j = 0; j < paramNodes.getLength(); j += 1) {
                                Node paramNode = paramNodes.item(j);
                                if (paramNode.getNodeType() == Node.ELEMENT_NODE) {
                                    params[j] = paramNode.getTextContent();
                                }
                            }
                        }
                        agents.add(Agents.getAgent(name, params));
                    }
                }

                List<double[]> rewardsData = new ArrayList<double[]>();
                List<double[]> avgMoveTimeData = new ArrayList<double[]>();
                for (int i = 0; i < agents.size(); i += 1) {
                    rewardsData.add(new double[nSimulations]);
                    avgMoveTimeData.add(new double[nSimulations]);
                }
                Arbiter<?, ?> arbiter = new Arbiter(world.getInitialState(), world, agents);
                for (int i = 0; i < nSimulations; i += 1) {
                    arbiter.reset();
                    try {
                        int count = 0;
                        while (!arbiter.getWorld().isTerminalState()) {
                            arbiter.step();
                            count += 1;
                            for (int j = 0; j < agents.size(); j += 1) {
                                rewardsData.get(j)[i] += arbiter.getReward(j);
                                avgMoveTimeData.get(j)[i] += arbiter.getDecisionTime(j);
                            }
                        }
                        for (int j = 0; count != 0 && j < agents.size(); j += 1) {
                            avgMoveTimeData.get(j)[i] /= count;
                        }
                    } catch (InterruptedException e) {}
                }

                // output
                StringBuilder output = new StringBuilder();
                
                if (nSimulations == 1) {
                    output.append(arbiter.getHistory());
                }

                // #nSimulations - world - simulator
                output.append("#");
                output.append(nSimulations);
                output.append(" - ");
                output.append(world.getClass().getSimpleName());
                output.append(" - ");
                output.append(simulatedWorld.getClass().getSimpleName());
                output.append("\n");

                DecimalFormat df = new DecimalFormat("#.###");
                int buffer = 10;
                for (int j = 0; j < agents.size(); j++) {
                    output.append(agents.get(j).toString());
                    output.append("\n");
                    double[] rewards = rewardsData.get(j);
                    double[] avgMoveTimes = avgMoveTimeData.get(j);
                    Mean mean = new Mean();
                    String temp = df.format(mean.evaluate(rewards));
                    for (int k = temp.length(); k < buffer; k++)
                        output.append(" ");
                    output.append(temp + ",");
                    StandardDeviation sd = new StandardDeviation();
                    temp = df.format(sd.evaluate(rewards));
                    for (int k = temp.length(); k < buffer; k++)
                        output.append(" ");
                    output.append(temp + ",");
                    temp = df.format(mean.evaluate(avgMoveTimes));
                    for (int k = temp.length(); k < buffer; k++)
                        output.append(" ");
                    output.append(temp + ",");
                    temp = df.format(sd.evaluate(avgMoveTimes));
                    for (int k = temp.length(); k < buffer; k++)
                        output.append(" ");
                    output.append(temp);
                    output.append("\n");
                }
                
                if (args.length == 2) {
                    recordResults(args[1], output.toString());
                } else {
                    System.out.println(output.toString());
                }
            } catch (ParserConfigurationException e) {
                e.printStackTrace();
            } catch (SAXException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else
            System.out.println("Illegal arguments\nusage: java -jar DomainTest.jar test_filepath [output_filepath]");
    }

    private String getTagValue(String tagName, Element element) {
        NodeList nList = element.getElementsByTagName(tagName).item(0).getChildNodes();
        return ((Node) nList.item(0)).getNodeValue();
    }

    private void recordResults(String filepath, String results) {
        //if it doesnt exist create it
        //new File(filepath);
        try {
            BufferedWriter output = new BufferedWriter(new FileWriter(filepath, true));
            output.write(results);
            output.close();
        } catch (IOException exception) {
            throw new IllegalArgumentException("Could not write to " + filepath);
        }
    }

    private Simulator<?, ?> selectSimulator(String name, String[] args) {
        if (name.equalsIgnoreCase("backgammon"))
            return new BackgammonSimulator();
        else if (name.equalsIgnoreCase("biniax"))
            return new BiniaxSimulator();
        else if (name.equalsIgnoreCase("havannah"))
            return new HavannahSimulator(Integer.valueOf(args[0]), TurnType.SEQUENTIAL);
        else if (name.equalsIgnoreCase("hex"))
        	return new HexSimulator(Integer.valueOf(args[0]), TurnType.SEQUENTIAL);
        else if (name.equalsIgnoreCase("mathax"))
        	return new MathaxSimulator();
        else
            throw new IllegalArgumentException("invalid simulator: " + name);
    }

    private void registerAgents() {
        try {
            Class.forName("com.castlefrog.agl.agents.ConsoleAgentProvider");
            Class.forName("com.castlefrog.agl.agents.RandomAgentProvider");
            Class.forName("com.castlefrog.agl.agents.UctAgentProvider");
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    public static void main(String[] args) {
        new DomainTest(args);
    }
}
