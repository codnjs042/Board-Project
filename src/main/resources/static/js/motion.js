$(function(){
    const error = document.body.getAttribute('data-error');
    if(error){
        alert(error);
    }

    $("button:contains('좋아요')").on("click", function(){
        if($(this).prop("data-like")){
            $(this).css({
            "border":"none",
            "background":"gray",
            "color":"white"
            })
        }
        else{
            $(this).css({
                "border":"1px solid gray",
                "background":"none",
                "color":"gray"
            })
        }

    })

    $("th>input[type='checkbox']").on("click", function(){
        if ($(this).prop("checked")){
            $(this).prop("checked", true);
            $("td>input[type='checkbox']").prop("checked", true);
            $("td>input[type='hidden']").prop("disabled", false);
        }
        else{
            $(this).prop("checked", false);
            $("td>input[type='checkbox']").prop("checked", false);
            $("td>input[type='hidden']").prop("disabled", true);
        }
    });

    $("td>input[type='checkbox']").on("click", function(){
        if ($(this).prop("checked")) {
            $(this).next("input").prop("disabled", false);
        }
        else {
            $(this).next("input").prop("disabled", true);
        }
    });

    var li = null;
    $(document).on("focus",".comment_input", function(){
        if($("div").hasClass("new_comment_input")){
            $(".comment_reply").text("답글 쓰기");
            $(".comment_reply").next().remove();
            if (li) li.html(backup);
            $("li").attr("data-mode", "read");
        }
    });

    $(document).on("click", ".comment_edit", function(){
        if($(this).closest("li").attr("data-mode")=="read"){
            if($("div").hasClass("new_comment_input")){
                $(".comment_reply").text("답글 쓰기");
                $(".comment_reply").next().remove();
                if (li) li.html(backup);
                $("li").attr("data-mode", "read");
            }
            li = $(this).closest("li");
            backup = li.html();
            li.html(backup);
            var commentId = li.attr("data-id");
            var postId = $("#postSection").data("id");
            var text = li.find("span").eq(1).text();
            var input = $(".comment_input").clone(true, true)
            input.removeClass("comment_input").addClass("new_comment_input");
            input.find("form").attr("action", `/post/${postId}/comment/${commentId}`);
            input.find("textarea").val(text);
            input.find("button").after("<button>취소</button>");

            li.html(input);
            li.attr("data-mode", "edit");
        }else{
            li.html(backup);
            li.attr("data-mode", "read");
        }
    });

    $(document).on("click", ".comment_reply", function(){
        if($(this).text()=="답글 쓰기"){
            if($("div").hasClass("new_comment_input")){
                $(".new_comment_input").prev(".comment_reply").text("답글 쓰기");
                $(".new_comment_input").remove();
                if (li) li.html(backup);
                $("li").attr("data-mode", "read");
            }
            $(this).text("답글 닫기");
            var input = $(".comment_input").clone(true, true)
            input.removeClass("comment_input").addClass("new_comment_input");
            var id = $(this).closest("li").attr("data-id");
            input.find("form").append(`<input type="hidden" name="parentId" value="${id}">`);
            $(this).after(input);
        }else{
            $(this).text("답글 쓰기");
            $(this).next().remove();
        }
    });

    $(document).on("click", "button:contains('취소')", function(){
        if (li) li.html(backup);
        li.attr("data-mode", "read");
    });
});
//$(function(){
//    var user = $("body").data("username");
//    var postId = $("body").data("post-id");
//
//    function renderComment(comment) {
//        var date = new Date(comment.createdAt);
//
//        var commentBlock = $("<li></li>")
//            .attr("data-comment-id", comment.id)
//            .append($("<div></div>")
//                .append($("<p></p>")
//                    .append($("<span></span>").text(comment.authorName + " "))
//                    .append($("<span></span>").text(date.toLocaleString()))
//            ),
//            $("<div></div>").append($("<p></p>").text(comment.comment)),
//            $("<div class='replace'></div>"),
//            $("<hr>")
//        );
//        if (user) {
//            var userDiv = $("<div></div>");
////            .append(
////                $("<button></button>").addClass("btn-text reply").text("답글 쓰기")
////            );
//            if (user === comment.authorName) {
//                userDiv.append(
//                    $("<button class='btn-text'></button>").text("수정"),
//                    $("<button class='btn-text'></button>").text("삭제")
//                );
//            }
//            commentBlock.find(".replace").replaceWith(userDiv);
//        }
//
////        if (comment.child && comment.child.length > 0) {
////            var replyList = $("<ol style='list-style:none;'></ol>");
////            $.each(comment.child, function(i, reply){
////                replyList.append(renderComment(reply));
////            });
////            commentBlock.append(replyList);
////        }
//        return commentBlock;
//    }
//
//    $.getJSON("/post/" + postId + "/comment", function(comments){
//        var html = $("<div id='comments'></div>");
//        var list = $("<ol style='list-style:none;'></ol>");
//        $.each(comments, function(index, comment){
//            list.append(renderComment(comment));
//        });
//        html.append(list);
//        $("#comment-sec").html(html);
//    });
//
//
////    $(document).on("click", ".reply", function(){
////        $(".comment_input.clone").remove();
////        $(".reply").not($(this)).text("답글 쓰기");
////
////        if($(this).text() === "답글 쓰기"){
////            var parentList = $(this).closest("li");
////            var parentId = parentList.data("comment-id");
////            var input = $(".comment_input").clone(true).addClass("clone");
////            input.children("form").append(
////                $("<input>")
////                    .attr("type", "hidden")
////                    .attr("name", "parentId")
////                    .attr("value", parentId)
////            );
////            var replyList = parentList.children("ol");
////            if(replyList.length === 0){
////                replyList = $("<ol style='list-style:none;'></ol>");
////                parentList.append(replyList);
////            }
////            replyList.prepend(input);
////            $(this).text("답글 닫기");
////        }
////        else {
////            $(this).text("답글 쓰기");
////            $(this).closest("li").find(".comment_input.clone").remove();
////        }
////    });
//});
