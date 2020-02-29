package lostnfound.university.herzen.lostnfound.components

import android.content.Context
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.AttributeSet
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.ProgressBar

class PagingRecyclerView<ItemViewHolder : RecyclerView.ViewHolder, Item>(
    context: Context,
    attributeSet: AttributeSet
) : RecyclerView(context, attributeSet) {
    // Required parameters
    var dataProvider: () -> ArrayList<Item?> = { ArrayList() }
    var loadData: (
        searchText: String?, pageNumber: Int?, preExecuteCallback: (() -> Unit)?,
        postExecuteCallback: (result: Any) -> Unit
    ) -> Unit = { _, _, preExecuteCallback, postExecuteCallback ->
        preExecuteCallback?.invoke()
        postExecuteCallback.invoke(true)
    }

    private var pageSize: Int = 10
    private var nextPageNumber = 0
    private var isLoadingData = false
    private var dataSet: ArrayList<Item?> = ArrayList()

    init {
        layoutManager = LinearLayoutManager(context)

        addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                val linearLayoutManager = recyclerView.layoutManager as LinearLayoutManager
                if (!isLoadingData) {
                    if (linearLayoutManager.findLastCompletelyVisibleItemPosition() == adapter?.itemCount?.minus(1)) {
                        loadNextPage()
                    }
                }
            }
        })
    }

    fun loadNextPage() {
        loadData(null, nextPageNumber++,
            {
                isLoadingData = true
                dataSet.add(null)
                refreshAdapterDataSet()
                adapter?.notifyItemInserted(dataSet.size - 1)
                scrollToPosition(dataSet.size - 1)
            },
            {
                dataSet.clear()
                dataSet.addAll(dataProvider.invoke())
                refreshAdapterDataSet()
                adapter?.notifyDataSetChanged()
                isLoadingData = false
            }
        )
    }

    private fun refreshAdapterDataSet() {
        if (adapter != null)
            (adapter as PagingAdapter<ItemViewHolder, Item>).replaceDataSet(dataSet)
    }

    class PagingAdapter<ItemViewHolder : ViewHolder, Item>(
        private val itemViewLayoutId: Int,
        private val itemViewCreator: (view: View) -> ItemViewHolder,
        private val itemViewBinder: (holder: ItemViewHolder, item: Item) -> Unit
    ) : Adapter<ViewHolder>() {

        private var dataSet: ArrayList<Item?> = ArrayList()

        fun replaceDataSet(newData: ArrayList<Item?>) {
            dataSet.clear()
            dataSet.addAll(newData)
        }

        override fun getItemViewType(position: Int): Int {
            if (dataSet[position] == null)
                return LOAD_VIEW_TYPE
            return ITEM_VIEW_TYPE
        }

        class LoadViewHolder(val view: View) : RecyclerView.ViewHolder(view) {
            private var progressBar: ProgressBar = ProgressBar(view.context)

            init {
                if (view is LinearLayout) {
                    view.addView(progressBar)
                    view.orientation = LinearLayout.VERTICAL
                    view.gravity = Gravity.CENTER

                    view.layoutParams = LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                    )
                }
            }
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            return if (viewType == ITEM_VIEW_TYPE)
                itemViewCreator.invoke(
                    LayoutInflater.from(parent.context).inflate(
                        itemViewLayoutId,
                        parent,
                        false
                    )
                )
            else
                LoadViewHolder(LinearLayout(parent.context))
        }

        override fun getItemCount() = dataSet.size

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            if (holder.itemViewType == ITEM_VIEW_TYPE) {
                itemViewBinder.invoke(
                    holder as @kotlin.ParameterName(name = "holder") ItemViewHolder,
                    dataSet[position]!!
                )
            }
        }

        companion object {
            const val ITEM_VIEW_TYPE = 0
            const val LOAD_VIEW_TYPE = 1
        }
    }
}