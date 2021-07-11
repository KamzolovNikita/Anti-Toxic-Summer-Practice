package com.example.android.bellmanford.algorithm

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.android.bellmanford.R

class AlgorithmStepAdapter : RecyclerView.Adapter<AlgorithmStepAdapter.TextItemViewHolder>() {

    var data = listOf<Step>()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    override fun getItemCount() = data.size

    override fun onBindViewHolder(holder: TextItemViewHolder, position: Int) {
        val item = data[position]
        holder.bind(item)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TextItemViewHolder {
        return TextItemViewHolder.from(parent)
    }

    class TextItemViewHolder(val textView: TextView) : RecyclerView.ViewHolder(textView) {

        fun bind(item: Step) {
            when (item.stepMsg) {
                StepMsg.NORMAL -> {
                    val firstWeightParams = if(item.stepData.firstWeightParam != Int.MAX_VALUE) item.stepData.firstWeightParam.toString() else "∞"
                    textView.text = textView.context.resources.getString(
                        R.string.txt_algorithm_step_default,
                        item.stepNumber,
                        item.stepData.firstVertexParam,
                        item.stepData.secondVertexParam,
                        firstWeightParams,
                        item.stepData.firstVertexParam,
                        item.stepData.secondVertexParam,
                        item.stepData.secondWeightParam
                    )
                }

                StepMsg.NEGATIVE_CYCLE -> {
                    textView.text =
                        textView.context.resources.getString(
                            R.string.txt_algorithm_step_negative_cycle,
                            item.stepNumber)
                }

                StepMsg.PATH -> {
                    if(item.stepData.firstWeightParam != Int.MAX_VALUE) {
                        textView.text = textView.context.resources.getString(
                            R.string.txt_algorithm_path,
                            item.stepNumber,
                            item.stepData.firstVertexParam,
                            item.stepData.secondVertexParam,
                            item.stepData.firstWeightParam
                        )
                    }
                    else {
                        textView.text = textView.context.resources.getString(
                            R.string.txt_algorithm_no_path,
                            item.stepNumber,
                            item.stepData.firstVertexParam,
                            item.stepData.secondVertexParam
                        )
                    }
                }
            }
        }

        companion object {
            fun from(parent: ViewGroup): TextItemViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val view = layoutInflater
                    .inflate(R.layout.text_item_view, parent, false) as TextView

                return TextItemViewHolder(view)
            }
        }
    }
}