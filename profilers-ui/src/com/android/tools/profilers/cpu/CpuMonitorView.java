/*
 * Copyright (C) 2016 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.android.tools.profilers.cpu;

import com.android.tools.adtui.AxisComponent;
import com.android.tools.adtui.Choreographer;
import com.android.tools.adtui.LegendComponent;
import com.android.tools.adtui.TabularLayout;
import com.android.tools.adtui.chart.linechart.LineChart;
import com.android.tools.adtui.chart.linechart.LineConfig;
import com.android.tools.adtui.common.formatter.SingleUnitAxisFormatter;
import com.android.tools.adtui.model.Range;
import com.android.tools.adtui.model.RangedContinuousSeries;
import com.android.tools.profilers.ProfilerColors;
import com.android.tools.profilers.ProfilerMonitorView;
import com.intellij.ui.components.JBPanel;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Collections;

import static com.android.tools.profilers.ProfilerLayout.*;

public class CpuMonitorView extends ProfilerMonitorView<CpuMonitor> {

  private static final SingleUnitAxisFormatter CPU_USAGE_AXIS = new SingleUnitAxisFormatter(1, 2, 10, "%");

  public CpuMonitorView(@NotNull CpuMonitor monitor) {
    super(monitor);
  }

  @Override
  protected void populateUi(JPanel container, Choreographer choreographer) {
    container.setLayout(new TabularLayout("*", "*"));

    Range viewRange = getMonitor().getTimeline().getViewRange();
    Range dataRange = getMonitor().getTimeline().getDataRange();

    final JLabel label = new JLabel(getMonitor().getName());
    label.setBorder(MONITOR_LABEL_PADDING);
    label.setVerticalAlignment(JLabel.TOP);

    // Cpu usage is shown as percentages (e.g. 0 - 100) and no range animation is needed.
    Range leftYRange = new Range(0, 100);
    final JPanel axisPanel = new JBPanel(new BorderLayout());
    axisPanel.setOpaque(false);
    AxisComponent.Builder builder = new AxisComponent.Builder(leftYRange, CPU_USAGE_AXIS,
                                                              AxisComponent.AxisOrientation.RIGHT)
      .showAxisLine(false)
      .showMax(true)
      .showUnitAtMax(true)
      .setMarkerLengths(MARKER_LENGTH, MARKER_LENGTH)
      .clampToMajorTicks(true)
      .setMargins(0, Y_AXIS_TOP_MARGIN);
    final AxisComponent leftAxis = builder.build();
    axisPanel.add(leftAxis, BorderLayout.WEST);

    final JPanel lineChartPanel = new JBPanel(new BorderLayout());
    lineChartPanel.setOpaque(false);
    lineChartPanel.setBorder(BorderFactory.createEmptyBorder(Y_AXIS_TOP_MARGIN, 0, 0, 0));
    final LineChart lineChart = new LineChart();
    RangedContinuousSeries cpuSeries = new RangedContinuousSeries("CPU", viewRange, leftYRange, getMonitor().getThisProcessCpuUsage());
    lineChart.addLine(cpuSeries, new LineConfig(ProfilerColors.CPU_USAGE).setFilled(true));
    lineChartPanel.add(lineChart, BorderLayout.CENTER);

    final LegendComponent legend = new LegendComponent(LegendComponent.Orientation.HORIZONTAL, LEGEND_UPDATE_FREQUENCY_MS);
    legend.setLegendData(Collections.singletonList(lineChart.createLegendRenderData(cpuSeries, CPU_USAGE_AXIS, dataRange)));

    final JPanel legendPanel = new JBPanel(new BorderLayout());
    legendPanel.setOpaque(false);
    legendPanel.add(label, BorderLayout.WEST);
    legendPanel.add(legend, BorderLayout.EAST);

    choreographer.register(lineChart);
    choreographer.register(leftAxis);
    choreographer.register(legend);

    container.add(legendPanel, new TabularLayout.Constraint(0, 0));
    container.add(leftAxis, new TabularLayout.Constraint(0, 0));
    container.add(lineChartPanel, new TabularLayout.Constraint(0, 0));
    container.addMouseListener(new MouseAdapter() {
      @Override
      public void mouseReleased(MouseEvent e) {
        getMonitor().expand();
      }
    });
  }
}
