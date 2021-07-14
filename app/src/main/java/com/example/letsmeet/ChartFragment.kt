@file:Suppress("DEPRECATION")

package com.example.letsmeet

import android.graphics.Color
import android.os.Bundle
import android.preference.PreferenceManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.github.mikephil.charting.animation.Easing
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.data.*
import com.github.mikephil.charting.utils.ColorTemplate
import java.util.*


@Suppress("DEPRECATION")
class ChartFragment : Fragment() {


    lateinit var rootview: View

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
         rootview= inflater.inflate(R.layout.fragment_chart, container, false)
val barChart= rootview.findViewById(R.id.tabbarChart) as BarChart
val pieChart=rootview.findViewById(R.id.tabpieChart) as PieChart

        setChart(barChart,pieChart)
        return rootview
    }


    private fun setChart(barChart:BarChart,pieChart:PieChart) {
        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(rootview.context)
         val added=sharedPreferences.getInt("Added",0).toFloat()
        val accepted=sharedPreferences.getInt("Accepted",0).toFloat()
        val removed=sharedPreferences.getInt("Removed",0).toFloat()
         val requested=sharedPreferences.getInt("Requested",0).toFloat()
         val rescheduled=sharedPreferences.getInt("Rescheduled",0).toFloat()
         val completed=sharedPreferences.getInt("Completed",0).toFloat()
         val total=sharedPreferences.getInt("Total",0).toFloat()

        val entries = ArrayList<BarEntry>()
        entries.add(BarEntry(added, 0))
        entries.add(BarEntry(accepted, 1))
        entries.add(BarEntry(removed, 2))
        entries.add(BarEntry(requested, 3))
        entries.add(BarEntry(rescheduled, 4))
        entries.add(BarEntry(completed, 5))
        entries.add(BarEntry(total, 6))

        val entries2 = ArrayList<Entry>()
        entries2.add( Entry(added, 0))
        entries2.add(Entry(accepted, 1))
        entries2.add(Entry(removed, 2))
        entries2.add(Entry(requested, 3))
        entries2.add(Entry(rescheduled, 4))
        entries2.add(Entry(completed, 5))
        entries2.add(Entry(total, 6))

        val barDataSet = BarDataSet(entries,null)
        val piedataSet = PieDataSet(entries2, null)

        val labels = ArrayList<String>()
        labels.add("Initiated")
        labels.add("Accepted")
        labels.add("Removed")
        labels.add("Requested")
        labels.add("Rescheduled")
        labels.add("Completed")
        labels.add("Total")

        val data = BarData(labels, barDataSet)
        val data2 = PieData(labels, piedataSet)

        // add a lot of colors
        val colors = ArrayList<Int>()

        for (c in ColorTemplate.VORDIPLOM_COLORS) colors.add(c)

        for (c in ColorTemplate.JOYFUL_COLORS) colors.add(c)

        for (c in ColorTemplate.COLORFUL_COLORS) colors.add(c)

        for (c in ColorTemplate.LIBERTY_COLORS) colors.add(c)

        for (c in ColorTemplate.PASTEL_COLORS) colors.add(c)

        colors.add(ColorTemplate.getHoloBlue())


        barChart.data = data // set the data and list of lables into chart
        pieChart.data=data2
          barChart.setDrawBarShadow(false)
        pieChart.isDrawHoleEnabled =true
        pieChart.setDescription("Chart")
        pieChart.setDescriptionTextSize(8f)
pieChart.setDescriptionPosition(1f,2f)

       /* pieChart.transparentCircleRadius = 58f
        pieChart.holeRadius = 58f*/
        piedataSet.colors = colors
        barDataSet.colors = colors
        pieChart.animateY(5000, Easing.EasingOption.EaseInCirc)
        barChart.animateY(5000, Easing.EasingOption.EaseOutBounce)

   //<------------------------------------------------------------------------------------------------------------>//
        piedataSet.sliceSpace = 3f
        piedataSet.selectionShift = 5f

        data2.setValueTextSize(8f)

        data2.setValueTextColor(Color.WHITE)

        // undo all highlights

        // undo all highlights
        pieChart.highlightValues(null)

        pieChart.setUsePercentValues(true)
       // pieChart.getDescription().setEnabled(false)
        pieChart.setExtraOffsets(5f, 10f, 5f, 5f)

        pieChart.dragDecelerationFrictionCoef = 0.95f

       // pieChart.setCenterTextTypeface(tfLight)
        pieChart.centerText = "LetsMeet"

        pieChart.isDrawHoleEnabled = true
        pieChart.setHoleColor(Color.WHITE)

        pieChart.setTransparentCircleColor(Color.WHITE)
        pieChart.setTransparentCircleAlpha(110)

        pieChart.holeRadius = 48f
        pieChart.transparentCircleRadius = 51f

        pieChart.setDrawCenterText(true)

        pieChart.rotationAngle = 0f
        // enable rotation of the chart by touch
        // enable rotation of the chart by touch
        pieChart.isRotationEnabled = true
        pieChart.isHighlightPerTapEnabled = true
        pieChart.invalidate()

        // chart.setUnit(" €");
        // chart.setDrawUnitsInChart(true);

        // add a selection listener

        // chart.setUnit(" €");
        // chart.setDrawUnitsInChart(true);

        // add a selection listener
       // pieChart.setOnChartValueSelectedListener(rootview.context)
        //<------------------------------------------------------------------------------------------------------------>//


    }
}