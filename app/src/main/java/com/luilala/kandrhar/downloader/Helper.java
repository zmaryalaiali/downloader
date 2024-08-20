package com.luilala.kandrhar.downloader;

public class Helper {

//    RequestSearchResult request = new RequestSearchResult("search query")
//            // filters
//            .type(TypeField.VIDEO)                 // Videos only
//            .format(FormatField._3D,
//                    FormatField.HD)                    // 3D HD videos
//            .match(FeatureField.SUBTITLES)         // with subtitles
//            .during(DurationField.OVER_20_MINUTES) // more than 20 minutes videos
//            .uploadedThis(UploadDateField.MONTH)   // uploaded this month
//
//            // other parameters
//            .forceExactQuery(true)                 // avoid auto correction
//            .sortBy(SortField.VIEW_COUNT);         // results sorted by view count
//    // or
//    RequestSearchResult request = new RequestSearchResult("search query")
//            .filter(
//                    TypeField.VIDEO,
//                    FormatField.HD,
//                    (...)
//    UploadDateField.MONTH);
//
//    SearchResult result = downloader.search(request).data();
//
//// retrieve next result (20 items max per continuation)
//if (result.hasContinuation()) {
//        RequestSearchContinuation nextRequest = new RequestSearchContinuation(result);
//        SearchResult continuation = downloader.searchContinuation(nextRequest).data();
//    }
//
//// a query is suggested, get its result
//if (result.suggestion() != null) {
//        System.out.println(result.suggestion().query()); // suggested query
//        RequestSearchable suggestedRequest = new RequestSearchable(result.suggestion());
//        SearchResult suggestedResult = downloader.search(suggestedRequest).data();
//    }
//
//// query refinements
//if (result.refinements() != null) {
//        System.out.println(result.refinements().get(0).query()); // refinement query
//        RequestSearchable refinedRequest = new RequestSearchable(result.refinements().get(0));
//        SearchResult refinedResult = downloader.search(refinedRequest).data();
//    }
//
//// the query has been auto corrected, force original query
//if (result.isAutoCorrected()) {
//        System.out.println(result.autoCorrectedQuery()); // corrected query
//        SearchResult forcedResult = downloader.search(request.forceExactQuery(true)).data();
//    }
//
//// details
//System.out.println(result.estimatedResults());
//
//    // items, 20 max per result (+ possible shelves on first result)
//    List<SearchResultItem> items = result.items();
//    List<SearchResultVideoDetails> videos = result.videos();
//    List<SearchResultChannelDetails> channels = result.channels();
//    List<SearchResultPlaylistDetails> playlists = result.playlists();
//    List<SearchResultShelf> shelves = result.shelves();
//
//    // item cast
//    SearchResultItem item = items.get(0);
//switch (item.type()) {
//        case VIDEO:
//            System.out.println(item.asVideo().description());
//            break;
//        case SHELF:
//            for (SearchResultVideoDetails video : item.asShelf().videos()) {
//                System.out.println(video.author());
//            }
//            break;
//        (...)
//    }

}
