package com.wallaceespindola.modernization.posting;

import java.util.Optional;

/** Safe posting: the port to the ledger, idempotent by key and able to answer a status inquiry. */
public interface PostingGateway {

    PostingResult post(PostingRequest request);

    Optional<PostingStatus> status(String idempotencyKey);
}
