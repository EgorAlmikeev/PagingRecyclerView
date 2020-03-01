package projects.ealmikeew.pagingrecyclersampleproject

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.RecyclerView
import android.view.View
import android.widget.SearchView
import android.widget.TextView

class MainActivity : AppCompatActivity() {

    // paging list item
    class MyViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var title: TextView = view.findViewById(R.id.title)
        var description: TextView = view.findViewById(R.id.description)
    }

    // creates an instance of MyViewHolder
    private fun myViewHolderCreator(view: View) = MyViewHolder(view)

    // passes data from model to a view
    private fun myViewHolderBinder(holder: MyViewHolder, model: MyModel) {
        holder.apply {
            title.text = model.title
            description.text = model.description
        }
    }

    private lateinit var paging: PagingRecyclerView<MyViewHolder, MyModel>
    private lateinit var search: SearchView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        setupViews()
        paging.loadNextPage()
    }

    private fun setupViews() {
        search = findViewById(R.id.search)
        paging = findViewById(R.id.paging)

        // Searching and filtering
        search.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                // set up a condition
                MyDataManager.updateSearchText(query)
                // restart paging since the list is going to change since the condition
                paging.restartPaging()
                // load first page with applied condition
                paging.loadNextPage()
                return true
            }

            override fun onQueryTextChange(query: String?) = true
        })

        search.setOnCloseListener {
            // disable the condition
            MyDataManager.updateSearchText(null)
            // restart paging since the list is going to change since the condition disabled
            paging.restartPaging()
            // load first page without a condition
            paging.loadNextPage()
            true
        }

        paging.dataLoader = MyDataManager::myDataLoader
        paging.dataProvider = MyDataManager::myDataProvider

        paging.adapter = PagingRecyclerView.PagingAdapter(
            R.layout.my_list_item,
            ::myViewHolderCreator,
            ::myViewHolderBinder
        )
    }
}
