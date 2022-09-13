package ca.uwaterloo.ofn.ui.theme

import android.net.Uri
import ca.uwaterloo.ofn.R
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ca.uwaterloo.ofn.model.MenuItem
import coil.compose.rememberAsyncImagePainter

@Composable
fun RoundImage(
    image: Painter,
    modifier: Modifier = Modifier
) {
    Image(
        painter = image,
        contentDescription = null,
        modifier = modifier
            .aspectRatio(1f, matchHeightConstraintsFirst = true)
            .border(
                width = 1.dp,
                color = Color.LightGray,
                shape = CircleShape
            )
            .padding(3.dp)
            .clip(CircleShape)
    )
}

@Composable
fun DrawerHeader(name: String?, email: String?, photoUrl: Uri?) {
    Row(verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
        .padding(20.dp)
    ){
//        RoundImage(image = painterResource(id = R.drawable.user),
//            modifier = Modifier
//                .size(80.dp)
//                .weight(3f)
//        )
        Image(
            painter = rememberAsyncImagePainter(photoUrl),
            contentDescription = null,
            modifier = Modifier
                .size(60.dp)
                .border(
                    width = 5.dp,
                    color = Color.LightGray,
                    shape = RectangleShape
                )
        )
        Column(modifier = Modifier
            .padding(20.dp)
            .align(Alignment.CenterVertically)){
            Text(text = "$name", fontWeight = FontWeight.Bold, fontSize = 15.sp)
            Text(text = "$name’s Produce Network", fontSize = 13.sp)
            Text(text = "$email", fontSize = 13.sp, color = Color(175,175,175), fontWeight = FontWeight.Bold)
        }
    }


}

@Composable
fun DrawerBody(
    items: List<MenuItem>,
    modifier: Modifier = Modifier,
    itemTextStyle: TextStyle = TextStyle(fontSize = 18.sp),
    onItemClick: (MenuItem) -> Unit
) {
    LazyColumn(modifier) {
        items(items) { item ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable {
                        onItemClick(item)
                    }
                    .padding(16.dp)
            ) {
                Icon(
                    imageVector = item.icon,
                    contentDescription = item.contentDescription
                )
                Spacer(modifier = Modifier.width(16.dp))
                Text(
                    text = item.title,
                    style = itemTextStyle,
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }

}