package com.akshita.bookify.activity

import android.annotation.SuppressLint
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.os.AsyncTask
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.room.Room
import com.akshita.bookify.R
import com.akshita.bookify.database.BookDatabase
import com.akshita.bookify.database.BookEntities
import com.squareup.picasso.Picasso

@Suppress("NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")
class BookDetails : AppCompatActivity() {
    private lateinit var txtBookDesc: TextView
    private lateinit var txtBookTitle: TextView
    private lateinit var txtBookRating: TextView
    private lateinit var txtBookAuthor: TextView
    private lateinit var txtBookPublisher: TextView
    private lateinit var txtBookCost: TextView
    private lateinit var txtBookBuyLink: TextView
    private lateinit var txtBookPages: TextView
    private lateinit var txtBookCategory: TextView
    private lateinit var txtBookLanguage: TextView
    private lateinit var imgBookIcon: ImageView
    private lateinit var progressLayout: RelativeLayout
    private lateinit var progressBar:ProgressBar
    private lateinit var btnFav: Button
    private lateinit var toolbar: androidx.appcompat.widget.Toolbar
    private var bookId: String = ""
    private var authorsName: String = ""
    private var publisher: String = ""
    private var pages: Int = 100
    private var language: String = ""
    private var price: Double = 100.0
    private var buyLink: String = ""
    private var bookDescription: String = ""
    private var bookName: String = ""
    private var bookRating: Double = 100.0
    private var thumbnail: String = ""
    private var category: String = ""

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_book_details)
        txtBookDesc = findViewById(R.id.txtbookDesc)
        txtBookTitle = findViewById(R.id.txtBookName)
        txtBookRating = findViewById(R.id.txtRating)
        txtBookPages = findViewById(R.id.txtPageCount)
        txtBookLanguage = findViewById(R.id.txtLanguage)
        txtBookPublisher = findViewById(R.id.txtPublisherName)
        txtBookCost = findViewById(R.id.txtCost)
        txtBookCategory = findViewById(R.id.txtCategoryName)
        txtBookBuyLink = findViewById(R.id.txtBuyLink)
        txtBookAuthor = findViewById(R.id.txtAuthorName)
        imgBookIcon = findViewById(R.id.imgBookIcon)
        toolbar = findViewById(R.id.Toolbar)
        btnFav=findViewById(R.id.btnfav)
        progressLayout=findViewById(R.id.ProgressLayout)
        progressBar=findViewById(R.id.ProgressBar)
        progressLayout.visibility=View.VISIBLE
        setUpToolbar()
        if (intent != null) {
            bookId = intent.getStringExtra("book_Id")
            bookDescription = intent.getStringExtra("book_desc")
            bookName = intent.getStringExtra("book_name")
            bookRating = intent.getDoubleExtra("book_rating", 0.0)
            buyLink = intent.getStringExtra("book_link")
            authorsName = intent.getStringExtra("book_author")
            publisher = intent.getStringExtra("book_publisher")
            pages = intent.getIntExtra("book_pages", 0)
            language = intent.getStringExtra("book_language")
            price = intent.getDoubleExtra("book_cost", 0.0)
            category = intent.getStringExtra("book_category")
            thumbnail = intent.getStringExtra("book_image")


        } else {
            finish()
            Toast.makeText(this@BookDetails, "Some unexpected error occurred", Toast.LENGTH_SHORT)
                .show()
        }
        if (bookId == "" && bookDescription == "") {
            finish()
            Toast.makeText(this@BookDetails, "Some unexpected error occurred", Toast.LENGTH_SHORT)
                .show()
        }
        progressLayout.visibility=View.GONE
        txtBookDesc.text = bookDescription
        txtBookAuthor.text = "Author Name: $authorsName"
        txtBookCategory.text = "Tag: $category"
        txtBookPublisher.text = "Publisher: $publisher"
        txtBookLanguage.text = "Language: $language"
        txtBookPages.text = "Page count: $pages"
        txtBookRating.text = bookRating.toString()
        txtBookTitle.text = bookName
        val imageUrl = thumbnail?.replace("http:/", "https:/")
        Picasso.get().load(imageUrl).error(R.mipmap.app_icon2).into(imgBookIcon)
        txtBookCost.text = "Price:Rs. $price"
        txtBookBuyLink.text = "Buy Link: $buyLink"
        txtBookBuyLink.setOnClickListener {
            val clipboard=getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
            val clip=ClipData.newPlainText("URL",buyLink)
            clipboard.setPrimaryClip(clip)
            Toast.makeText(this, "Link copied!!", Toast.LENGTH_SHORT).show()

        }
        val bookEntities = BookEntities(
            bookId,
            bookName,
            authorsName,
            publisher,
            bookDescription,
            pages,
            category,
            bookRating,
            thumbnail,
            language,
            price,
            buyLink
        )
        val checkFav = bookEntities.let { DbAsyncTaskClass(this , it, 1).execute() }
        val isFav = checkFav?.get()
        if (isFav!!) {
            btnFav.setBackgroundResource(R.drawable.ic_toggle_fav_fill)
        } else {
            btnFav.setBackgroundResource(R.drawable.ic_toggle_fav)
        }
          btnFav.setOnClickListener {
            if (!bookEntities.let { it1 -> DbAsyncTaskClass(this, it1, 1).execute().get() }) {
                val async = bookEntities.let { it1 -> DbAsyncTaskClass(
                    this,
                    it1,
                    2
                ).execute() }
                val result = async.get()
                if (result) {
                    Toast.makeText(this, "Book added to fav!!", Toast.LENGTH_SHORT).show()
                    btnFav.setBackgroundResource(R.drawable.ic_toggle_fav_fill)
                } else {
                    Toast.makeText(this, "Some error occurred!!", Toast.LENGTH_SHORT).show()
                }
            } else {
                val async = bookEntities.let { it1 -> DbAsyncTaskClass(
                    this,
                    it1,
                    3
                ).execute() }
                val result = async.get()
                if (result) {
                    Toast.makeText(this, "Book removed from fav!!", Toast.LENGTH_SHORT)
                        .show()
                    btnFav.setBackgroundResource(R.drawable.ic_toggle_fav)
                } else {
                    Toast.makeText(this, "Some error occurred!!", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun setUpToolbar(){
        setSupportActionBar(toolbar)
        supportActionBar?.title="Book Details"
        supportActionBar?.setHomeButtonEnabled(true)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
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
