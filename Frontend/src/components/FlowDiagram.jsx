// src/components/FlowDiagram.jsx
import React, { useCallback, useEffect, useMemo, useState } from "react";
import ReactFlow, {
  MiniMap,
  Controls,
  Background,
  useNodesState,
  useEdgesState,
  addEdge,
} from "reactflow";
import dagre from "dagre";
import { motion } from "framer-motion";
import 'reactflow/dist/style.css';

const nodeWidth = 170;
const nodeHeight = 48;

// dagre helper to compute layout
const dagreGraph = new dagre.graphlib.Graph();
dagreGraph.setDefaultEdgeLabel(() => ({}));
const getLayoutedElements = (nodes, edges, direction = "LR") => {
  const isHorizontal = direction === "LR";
  dagreGraph.setGraph({ rankdir: direction });

  const gNodes = nodes.map((n) =>
    dagreGraph.setNode(n.id, { width: nodeWidth, height: nodeHeight })
  );

  edges.forEach((e) => dagreGraph.setEdge(e.source, e.target));

  dagre.layout(dagreGraph);

  const layoutedNodes = nodes.map((n) => {
    const nodeWithPosition = dagreGraph.node(n.id);
    n.position = {
      x: nodeWithPosition.x - nodeWidth / 2,
      y: nodeWithPosition.y - nodeHeight / 2,
    };
    // return updated node object (make sure to keep data and id)
    return {
      ...n,
      position: n.position,
      style: {
        width: nodeWidth,
        height: nodeHeight,
        padding: 8,
        borderRadius: 10,
        boxShadow: "0 8px 20px rgba(0,0,0,0.12)",
        background: "#f7fcff",
        border: "1px solid rgba(25,118,210,0.08)",
      },
    };
  });

  const layoutedEdges = edges.map((e) => ({
    ...e,
    animated: true,
    style: { stroke: "#1976d2", strokeWidth: 2 },
    type: "smoothstep",
  }));

  return { nodes: layoutedNodes, edges: layoutedEdges };
};

export default function FlowDiagram({ calls = [], direction = "LR" }) {
  // build raw nodes & edges from calls
  const raw = useMemo(() => {
    const nodes = calls.map((c, i) => ({
      id: `n-${i}`,
      data: { label: c },
      position: { x: i * (nodeWidth + 40), y: 0 }, // initial fallback
    }));
    const edges = calls.slice(1).map((_, i) => ({
      id: `e-${i + 1}`,
      source: `n-${i}`,
      target: `n-${i + 1}`,
    }));
    return { nodes, edges };
  }, [calls]);

  const initialLayout = useMemo(() => getLayoutedElements(raw.nodes, raw.edges, direction), [raw, direction]);

  const [nodes, setNodes, onNodesChange] = useNodesState(initialLayout.nodes);
  const [edges, setEdges, onEdgesChange] = useEdgesState(initialLayout.edges);

  // if calls change, re-layout
  useEffect(() => {
    const layouted = getLayoutedElements(raw.nodes, raw.edges, direction);
    setNodes(layouted.nodes);
    setEdges(layouted.edges);
  }, [raw, direction, setNodes, setEdges]);

  const onConnect = useCallback(
    (params) => setEdges((eds) => addEdge({ ...params, animated: true, style: { stroke: "#1976d2" } }, eds)),
    [setEdges]
  );

  return (
    <div style={{ height: 360, width: "100%", borderRadius: 10, overflow: "hidden" }}>
      <ReactFlow
        nodes={nodes}
        edges={edges}
        onNodesChange={onNodesChange}
        onEdgesChange={onEdgesChange}
        onConnect={onConnect}
        fitView
        fitViewOptions={{ padding: 0.2 }}
        attributionPosition="bottom-left"
      >
        <MiniMap
          nodeStrokeColor={(n) => {
            return "#1976d2";
          }}
          nodeColor={(n) => "#e3f2fd"}
          nodeBorderRadius={2}
        />
        <Controls />
        <Background variant="dots" gap={16} size={1} />
      </ReactFlow>
    </div>
  );
}
