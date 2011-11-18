package org.gicentre.treemaps;

import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;

import org.gicentre.apps.hide.TreemapState.Layout;
import org.gicentre.data.summary.SummariseField;
import org.gicentre.data.summary.SummariseNode;
import org.gicentre.data.summary.SummariseNull;
import org.gicentre.treemappa.TreeMapNode;
import org.gicentre.treemappa.TreeMapProperties;
import org.gicentre.treemappa.TreeMappa;

import processing.core.PApplet;


/**For building treemaps
 * 
 * @author Aidan Slingsby, giCentre
 *
 */
public class TreemapBuilder {

	TreeMappa treeMappa;
	TreeMapProperties treeMapProperties; 
	
	public TreemapBuilder(TreeMapProperties treeMapProperties){
		this.treeMappa=new TreeMappa(treeMapProperties);
		this.treeMapProperties=treeMapProperties;
	}
	
	/**Constructor
	 * 
	 * @param summaryNode
	 * @param orderVars [state number][hierarchy number]
	 * @param sizeVars [state number][hierarchy number]
	 * @return
	 */
	public TreemapSummaryNode computeTreemap(SummariseNode summaryNode, SummariseField[][] orderVars,SummariseField[] sizeVars, Layout[] layouts,int offX,int offY){
		
		int startLevel=summaryNode.getLevel()+1;
		HashMap<SummariseNode, TreeMapNode> summaryNode2treemapNode = new HashMap<SummariseNode, TreeMapNode>();

		Iterator<SummariseNode> it =summaryNode.iterator();
		it.next();//ignore this (current) node.
		summaryNode2treemapNode.put(summaryNode, new TreemapSummaryNode("",0,null,null,summaryNode));
		while(it.hasNext()){
			SummariseNode node = it.next(); 
			int relativeLevel = node.getLevel()-startLevel;
			String label=node.getConditioningValueAsString();
			double order = node.getNaturalOrder();
			double orderX = node.getNaturalOrder();
			double orderY = node.getNaturalOrder();
			if (!layouts[relativeLevel].equals(Layout.TWO_DIMENSIONAL)&&!layouts[relativeLevel].equals(Layout.ABS_POSITION)){
				try{
					order=node.getSummaryAsDouble(orderVars[0][relativeLevel]);
				}
				catch (Exception e){
					//do nothing - uses the natural order (above)
				}
				if (layouts[relativeLevel].equals(Layout.ONE_DIM_STRIP)){
					treeMapProperties.setParameter("layout"+(relativeLevel+startLevel), "strip");
					treeMapProperties.setParameter("align"+(relativeLevel+startLevel), "free");
				}
				else if (layouts[relativeLevel].equals(Layout.ONE_DIM_ORDERED_SQUARIFIED)){
					treeMapProperties.setParameter("layout"+(relativeLevel+startLevel), "orderedSquarified");
					treeMapProperties.setParameter("align"+(relativeLevel+startLevel), "free");
				}
				else if (layouts[relativeLevel].equals(Layout.ONE_DIM_LEFT_RIGHT)){
					treeMapProperties.setParameter("layout"+(relativeLevel+startLevel), "sliceAndDice");
					treeMapProperties.setParameter("align"+(relativeLevel+startLevel), "horizontal");
				}
				else if (layouts[relativeLevel].equals(Layout.ONE_DIM_TOP_BOTTOM)){
					treeMapProperties.setParameter("layout"+(relativeLevel+startLevel), "sliceAndDice");
					treeMapProperties.setParameter("align"+(relativeLevel+startLevel), "vertical");
				}
			}
			else{
				try{
					orderX=node.getSummaryAsDouble(orderVars[0][relativeLevel]);
				}
				catch (Exception e){
					//do nothing - uses the natural order (above)
				}
				try{
					orderY=node.getSummaryAsDouble(orderVars[1][relativeLevel]);
				}
				catch (Exception e){
					//do nothing - uses the natural order (above)
				}
				treeMapProperties.setParameter("layout"+(relativeLevel+startLevel), "spatial");
				treeMapProperties.setParameter("align"+(relativeLevel+startLevel), "free");
			}
			Float size=1f;
			if (sizeVars[relativeLevel]!=null){
				size= node.getSummaryAsFloat(sizeVars[relativeLevel]);
			}
			if (size==null || Float.isNaN(size) || Float.isInfinite(size) || size==0){
				size=0.0000001f;//if zero, Treemappa ignores it.
			}
			TreeMapNode treeMapNode=new TreemapSummaryNode(label,order,size,(float)node.getNaturalOrder(),new Point2D.Double(orderX,orderY),node); //put natural order in colour so that if the order values are the same, the natural order is used (this is treemappa behaviour)
			summaryNode2treemapNode.get(node.getParent()).add(treeMapNode);
			summaryNode2treemapNode.put(node,treeMapNode);
		}
		treeMappa.setRoot(summaryNode2treemapNode.get(summaryNode));
		treeMappa.getRoot().sortDescendants();
		treeMappa.buildTreeMap();
		//move all the rectangles to their offsets
		Iterator<TreeMapNode> it1=treeMappa.getRoot().iterator(); 
		while(it1.hasNext()){
			Rectangle2D rectangle2D = it1.next().getRectangle();
			if (rectangle2D!=null){
				rectangle2D.setFrame(rectangle2D.getX()+offX,rectangle2D.getY()+offY,rectangle2D.getWidth(),rectangle2D.getHeight());
			}
		}

		//move any cartographic layouts to their cartographic position
		//First find min/max x/ys
		int depth=summaryNode.getDepth();
		Float[] minXs=new Float[depth];
		Float[] maxXs=new Float[depth];
		Float[] minYs=new Float[depth];
		Float[] maxYs=new Float[depth];
		Float[] maxHalfWHs=new Float[depth];//of treemap nodes (in pixels)
		it1=treeMappa.getRoot().iterator(); 
		while(it1.hasNext()){
			TreemapSummaryNode node=(TreemapSummaryNode)it1.next();
			int level=node.getLevel();
			if (layouts[level-1].equals(Layout.ABS_POSITION)){
				
				//find size for each node
				float halfWHs=(float)Math.sqrt((node.getRectangle().getWidth()*node.getRectangle().getHeight()))/2;
				if (maxHalfWHs[level-1]==null || halfWHs>maxHalfWHs[level-1]) maxHalfWHs[level-1]=halfWHs;

				//find x
				SummariseField orderXVar=orderVars[0][level-1];
				float x;
				if (orderXVar==null||orderXVar instanceof SummariseNull){
					x=node.getSummariseNode().getNaturalOrder();
				}
				else{
					try{
						x=node.getSummariseNode().getSummaryAsFloat(orderXVar);
					}
					catch (NullPointerException e){
						//nullpointer will be thrown in the no-data case - in this case, move the node offscreen 
						x=-99999;
					}
				}
				if (x!=-99999){
					if (minXs[level-1]==null || x<minXs[level-1]) minXs[level-1]=x;
					if (maxXs[level-1]==null || x>maxXs[level-1]) maxXs[level-1]=x;
				}
				
				//find y
				SummariseField orderYVar=orderVars[1][level-1];
				float y;
				if (orderYVar==null||orderYVar instanceof SummariseNull){
					y=node.getSummariseNode().getNaturalOrder();
				}
				else{
					try{
						y=node.getSummariseNode().getSummaryAsFloat(orderYVar);
					}
					catch (NullPointerException e){
						//nullpointer will be thrown in the no-data case - in this case, move the node offscreen 
						y=-99999;
					}

				}
				if (y!=-99999){				
					if (minYs[level-1]==null || y<minYs[level-1]) minYs[level-1]=y;
					if (maxYs[level-1]==null || y>maxYs[level-1]) maxYs[level-1]=y;
				}
			}
		}
		
		//then move them
		it1=treeMappa.getRoot().iterator(); 
		while(it1.hasNext()){
			TreemapSummaryNode node=(TreemapSummaryNode)it1.next();
			int level=node.getLevel();
			
			if (maxXs[level-1]!=null && maxYs[level-1]!=null){
				if (layouts[level-1].equals(Layout.ABS_POSITION)){

					Rectangle2D parentR=((TreemapSummaryNode)node.getParent()).getRectangle();
					Rectangle2D r=node.getRectangle();
					float x;
					SummariseField orderXVar=orderVars[0][level-1];
					if (orderXVar==null||orderXVar instanceof SummariseNull){
						x=node.getSummariseNode().getNaturalOrder();
					}
					else{
						try{
							x=node.getSummariseNode().getSummaryAsFloat(orderXVar);
						}
						catch(NullPointerException e){
							//nullpointer will be thrown in the no-data case - in this case, move the node offscreen 
							x=-99999;
						}
					}
					float y;
					SummariseField orderYVar=orderVars[1][level-1];
					if (orderYVar==null||orderYVar instanceof SummariseNull){
						y=node.getSummariseNode().getNaturalOrder();
					}
					else{
						try{
							y=node.getSummariseNode().getSummaryAsFloat(orderYVar);
						}
						catch(NullPointerException e){
							//nullpointer will be thrown in the no-data case - in this case, move the node offscreen 
							y=-99999;
						}
					}
					int wH=0;
					if (r.getWidth()*r.getHeight()>0){
						wH=(int)Math.sqrt(((r.getWidth()*r.getHeight())/2));
					}
					float screenX=PApplet.map(x,minXs[level-1],maxXs[level-1],(float)parentR.getMinX()+maxHalfWHs[level-1],(float)parentR.getMaxX()-maxHalfWHs[level-1]);
					float screenY=PApplet.map(y,minYs[level-1],maxYs[level-1],(float)parentR.getMaxY()-maxHalfWHs[level-1],(float)parentR.getMinY()+maxHalfWHs[level-1]);
					Rectangle2D parentOldR=(Rectangle2D)r.clone();
					r.setFrame(screenX-wH/2,screenY-wH/2,wH,wH);
					Rectangle2D parentNewR=r;
					//then move everything within this node
					for (TreeMapNode node1 : node){
						float newX=PApplet.map((float)node1.getRectangle().getX(),(float)parentOldR.getMinX(),(float)parentOldR.getMaxX(),(float)parentNewR.getMinX(),(float)parentNewR.getMaxX());
						float newY=PApplet.map((float)node1.getRectangle().getY(),(float)parentOldR.getMinY(),(float)parentOldR.getMaxY(),(float)parentNewR.getMinY(),(float)parentNewR.getMaxY());
						float newW=PApplet.map((float)node1.getRectangle().getWidth(),0,(float)parentOldR.getWidth(),0,(float)parentNewR.getWidth());
						float newH=PApplet.map((float)node1.getRectangle().getHeight(),0,(float)parentOldR.getHeight(),0,(float)parentNewR.getHeight());
						node1.getRectangle().setFrame(newX,newY,newW,newH);
					}
				}
			}
		}

		return (TreemapSummaryNode)treeMappa.getRoot();
	}
	
}
