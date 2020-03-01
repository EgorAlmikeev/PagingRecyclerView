# PagingRecyclerView

If you look for an easy way to implement pagination in your project so this is it!

## Preparation

1. add [PagingRecyclerView.kt](https://github.com/EgorAlmikeev/PagingRecyclerView/blob/master/PagingRecyclerView.kt) file into your project

2. place *PagingRecyclerView* view where necessary in your XML

   ```xml
   <path.to.the.file.PagingRecyclerView                                              						android:id="@+id/paging_recycler_view"
               android:layout_width="match_parent"
               android:layout_height="match_parent"
               android:scrollbars="vertical"/>
   ```

3. define a model which your paging suppose to work with

   ```kotlin
   data class MyModel() {
     // Model defenition
   }
   ```

4. define a *ViewHolder* for a regular list item as you always do

   ```kotlin
   class MyViewHolder(view: View) : RecyclerView.ViewHolder(view) {
           // ViewHolder defenition
   }
   ```

5. implement folowing functions: *dataProvider*, *dataLoader*, *itemViewHolderCreator*, *itemViewHolderBinder*

   ```kotlin
   // returns a ListArray<MyModel?> of your models, 
   // which you need to display
   fun myDataProvider() {
     return modelsList
   }
   
   // downloads or generates a new pack of models
   // and pushes it into the modelList returned by 
   // myDataProvider (should not work on the main thread)
   fun myDataLoader(
     pageNumber: Int, 
     pageSize: Int, 
     preExecuteCallback: (() -> Unit)?,
     postExecuteCallback: (result: Any) -> Unit) {
     // let it be your data downloader for example
     MyDownloader.download(pageNumber,
                           pageSize,
                           preExecuteCallback,
                           { newDataPack ->
                            modelList.addAll(newDataPack)
                            postExecuteCallback(true)
                           })
   }
   
   // returns an instance of your ViewHolder by view
   fun myItemViewHolderCreator(view: View) {
     return MyViewHolder(view)
   }
   
   // binds your ViewHolder and your model
   fun myItemViewHolderBinder(holder: MyViewHolder,
                              model: MyModel) {
     // imagine your holder contains a TextView named title
     holder.apply {
       title = model.title
     }
   }
   ```


## Action

Now, when all the preparations are done, lets make it work!

All you need to do is to create an instance of PagingRecyclerView correctly:

```kotlin
// somewhere in your code...
var paging: PagingRecyclerView<MyViewHolder, MyModel>
paging = findViewById<PagingRecyclerView<MyViewHolder, MyModel>>(R.id.paging_recycler_view).apply {
  
  dataProvider = myDataProvider
  dataLoader = myDataLoader
  
  adapter = PagingRecyclerView.PagingAdapter<MyViewHolder, MyModel>(
    R.layout.my_list_item_layout,
    myItemViewHolderCreator,
    myItemViewHolderBinder
  )
}

paging.loadNextPage()
```

Well, this code provides you a working paginated list of your models. It automatically loads next page using your *dataLoader*.

## Filtering and searching

If you want to implement searching or filtering options then you can use *restartPaging* method of *PagingRecyclerView*. It completely drops all paging setting such as number of next page, current data set, etc.

1. add your searching or filtering condition to the *dataLoader* function
2. when the list needs to be filtered, use *restartPaging* and *loadNextPage* methods to start paginating search results
3. when filters are dropped and your searching condition is disabled, use *restartPaging* and *loadNextPage* again to come back to a common list

You can also find the usage of it in a sample project.
