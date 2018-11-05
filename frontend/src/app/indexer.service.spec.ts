import { TestBed, inject } from '@angular/core/testing';

import { IndexerService } from './indexer.service';

describe('IndexerService', () => {
  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [IndexerService]
    });
  });

  it('should be created', inject([IndexerService], (service: IndexerService) => {
    expect(service).toBeTruthy();
  }));
});
