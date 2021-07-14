package com.example.letsmeet.map
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import androidx.core.text.HtmlCompat
import com.example.letsmeet.R
import com.here.android.mpa.search.AutoSuggest
import com.here.android.mpa.search.AutoSuggestPlace
import com.here.android.mpa.search.AutoSuggestQuery
import com.here.android.mpa.search.AutoSuggestSearch


class AutoSuggestAdapter(context: Context?, resource: Int, private val m_resultsList: List<AutoSuggest>) :
    ArrayAdapter<AutoSuggest>(context!!, resource, m_resultsList) {
    override fun getCount(): Int {
        return m_resultsList.size
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        var convertView = convertView
        val autoSuggest = getItem(position)
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.result_autosuggest_list_item, parent, false)
        }
        var tv: TextView? = null
        // set title
        tv = convertView!!.findViewById(R.id.title)
        tv.text = autoSuggest.title

        // set highlightedTitle
        tv = convertView.findViewById(R.id.highlightedTitle)
        tv.text = HtmlCompat.fromHtml(autoSuggest.highlightedTitle, HtmlCompat.FROM_HTML_MODE_LEGACY)

        // set request URL
        tv = convertView.findViewById(R.id.url)
        tv.text = "Url: " + autoSuggest.url

        // set Type
        tv = convertView.findViewById(R.id.type)
        tv.text = "Type: " + autoSuggest.type.name
        when (autoSuggest.type) {
            AutoSuggest.Type.PLACE -> {
                val autoSuggestPlace = autoSuggest as AutoSuggestPlace
                // set vicinity
                tv = convertView.findViewById(R.id.vicinity)
                tv.visibility = View.VISIBLE
                if (autoSuggestPlace.vicinity != null) {
                    tv.text = "Vicinity: " + autoSuggestPlace.vicinity
                } else {
                    tv.text = "Vicinity: nil"
                }

                // set category
                tv = convertView.findViewById(R.id.category)
                tv.visibility = View.VISIBLE
                if (autoSuggestPlace.category != null) {
                    tv.text = "Category: " + autoSuggestPlace.category
                } else {
                    tv.text = "Category: nil"
                }

                // set position
                tv = convertView.findViewById(R.id.position)
                tv.visibility = View.VISIBLE
                if (autoSuggestPlace.position != null) {
                    tv.text = "Position: " + autoSuggestPlace.position.toString()
                } else {
                    tv.text = "Position: nil"
                }

                // set boundaryBox
                tv = convertView.findViewById(R.id.boundaryBox)
                tv.visibility = View.VISIBLE
                if (autoSuggestPlace.boundingBox != null) {
                    tv.text = "BoundaryBox: " + autoSuggest.boundingBox
                        .toString()
                } else {
                    tv.text = "BoundaryBox: nil"
                }
            }
            AutoSuggest.Type.QUERY -> {
                val autoSuggestQuery = autoSuggest as AutoSuggestQuery
                // set completion
                tv = convertView.findViewById(R.id.vicinity)
                tv.text = "Completion: " + autoSuggestQuery.queryCompletion

                // set category
                tv = convertView.findViewById(R.id.category)
                tv.visibility = View.GONE

                // set position
                tv = convertView.findViewById(R.id.position)
                tv.visibility = View.GONE

                // set boundaryBox
                tv = convertView.findViewById(R.id.boundaryBox)
                tv.visibility = View.GONE
            }
            AutoSuggest.Type.SEARCH -> {
                val autoSuggestSearch = autoSuggest as AutoSuggestSearch
                // set vicinity
                tv = convertView.findViewById(R.id.vicinity)
                tv.visibility = View.GONE

                // set category
                tv = convertView.findViewById(R.id.category)
                tv.visibility = View.VISIBLE
                if (autoSuggestSearch.category != null) {
                    tv.text = "Category: " + autoSuggestSearch.category
                } else {
                    tv.text = "Category: nil"
                }

                // set position
                tv = convertView.findViewById(R.id.position)
                tv.visibility = View.VISIBLE
                if (autoSuggestSearch.position != null) {
                    tv.text = "Position: " + autoSuggestSearch.position.toString()
                } else {
                    tv.text = "Position: nil"
                }

                // set boundaryBox
                tv = convertView.findViewById(R.id.boundaryBox)
                tv.visibility = View.VISIBLE
                if (autoSuggestSearch.boundingBox != null) {
                    tv.text = "BoundaryBox: " + autoSuggestSearch.boundingBox.toString()
                } else {
                    tv.text = "BoundaryBox: nil"
                }
            }
            else -> {
            }
        }
        return convertView
    }

    override fun getItem(position: Int): AutoSuggest {
        return m_resultsList[position]
    }

}
