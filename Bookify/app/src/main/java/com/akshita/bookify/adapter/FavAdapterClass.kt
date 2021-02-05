package com.akshita.bookify.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.AsyncTask
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.recyclerview.widget.RecyclerView
import androidx.room.Room
import com.akshita.bookify.R
import com.akshita.bookify.activity.BookDetails
import com.akshita.bookify.database.BookDatabase
import com.akshita.bookify.database.BookEntities
import com.squareup.picasso.Picasso

class FavAdapterClass(val context: Context, private val bookList: List<BookEntities>):RecyclerView.Adapter<FavAdapterClass.FavViewHolder>() {
    class FavViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val txtBookName: TextView = view.findViewById(R.id.txtBookName)
        val txtAuthorName: TextView = view.findViewById(R.id.txtAuthorName)
        val imgBookIcon: ImageView = view.findViewById(R.id.imgBookIcon)
        val txtCategory: TextView = view.findViewById(R.id.txtCategoryName)
        val btnFav: Button = view.findViewById(R.id.btnfav)
        val txtRatings: TextView = view.findViewById(R.id.txtRating)
        val llcontent: LinearLayout = view.findViewById(R.id.llcontent)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FavViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.single_fav_row, parent, false)
        return FavViewHolder(view)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: FavViewHolder, position: Int) {
        val book = bookList[position]
        holder.txtBookName.text = book.title
            holder.txtAuthorName.text = book.authors
        holder.txtCategory.text = "Tag: " + book.categories
        holder.txtRatings.text = book.averageRating.toString()
        val imageUrl = book.thumbnail.replace("http:/", "https:/")
        Picasso.get().load(imageUrl).error(R.mipmap.app_icon2).into(holder.imgBookIcon)

        holder.llcontent.setOnClickListener {
            val intent = Intent(context, BookDetails::class.java)
            intent.putExtra("book_Id", book.id)
            intent.putExtra("book_desc",book.description)
            intent.putExtra("book_name",book.title)
            intent.putExtra("book_author",book.authors)
            intent.putExtra("book_publisher",book.publisher)
            intent.putExtra("book_pages",book.pageCount)
            intent.putExtra("book_category",book.categories)
            intent.putExtra("book_rating",book.averageRating)
            intent.putExtra("book_image",book.thumbnail)
            intent.putExtra("book_language",book.language)
            intent.putExtra("book_cost",book.amount)
            intent.putExtra("book_link",book.buyLink)
            context.startActivity(intent)
        }
       // val author= Gson().toJson()
        val bookEntities = BookEntities(
            book.id,
            book.title,
            book.authors,
            book.publisher,
            book.description,
            book.pageCount,
            book.categories,
            book.averageRating,
            book.thumbnail,
            book.language,
            book.amount,
            book.buyLink
        )
        val checkFav = bookEntities.let { DbAsyncTaskClass(context, it, 1).execute() }
        val isFav = checkFav?.get()
        if (isFav!!) {
            holder.btnFav.setBackgroundResource(R.drawable.ic_toggle_fav_fill)
        } else {
            holder.btnFav.setBackgroundResource(R.drawable.ic_toggle_fav)
        }
        holder.btnFav.setOnClickListener {
            if (!bookEntities.let { it1 -> DbAsyncTaskClass(context, it1, 1).execute().get() }) {
                val async = bookEntities.let { it1 -> DbAsyncTaskClass(context, it1, 2).execute() }
                val result = async.get()
                if (result) {
                    Toast.makeText(context, "Book added to fav!!", Toast.LENGTH_SHORT).show()
                    holder.btnFav.setBackgroundResource(R.drawable.ic_toggle_fav_fill)
                } else {
                    Toast.makeText(context, "Some error occurred!!", Toast.LENGTH_SHORT).show()
                }
            } else {
                val async = bookEntities.let { it1 -> DbAsyncTaskClass(context, it1, 3).execute() }
                val result = async.get()
                if (result) {
                    Toast.makeText(context, "Book removed from fav!!", Toast.LENGTH_SHORT)
                        .show()
                    holder.btnFav.setBackgroundResource(R.drawable.ic_toggle_fav)
                } else {
                    Toast.makeText(context, "Some error occurred!!", Toast.LENGTH_SHORT).show()
                }
            }
        }

    }

    override fun getItemCount(): Int {
        return bookList.size
    }

    class DbAsyncTaskClass(
        val context: Context,
        private val bookEntities: BookEntities,
        private val mode: Int
    ) :
        AsyncTask<Void, Void, Boolean>() {

        /*1.Check db if book is added to fav
     2.Save book to fav
     3.Remove book from fav*/
        private val db = Room.databaseBuilder(context, BookDatabase::class.java, "books-db").build()
        override fun doInBackground(vararg params: Void?): Boolean {
            when (mode) {
                1 -> {
                    val book = db.bookDao().getBookById(bookEntities.id)
                    db.close()
                    return book != null
                }
                2 -> {
                    db.bookDao().insertBook(bookEntities)
                    db.close()
                    return true
                }
                3 -> {
                    db.bookDao().deleteBook(bookEntities)
                    db.close()
                    return true
                }
            }
            return false
        }
    }
}