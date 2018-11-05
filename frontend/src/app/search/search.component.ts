import {Component, OnInit} from '@angular/core';
import {SearchService} from '../search.service';
import {SearchResponse} from '../search.response';
import {Observable, Subject} from 'rxjs';
import {SuggestResponse} from '../suggest.response';

import {
  debounceTime, distinctUntilChanged, switchMap
} from 'rxjs/operators';

@Component({
  selector: 'app-search',
  templateUrl: './search.component.html',
  styleUrls: ['./search.component.css']
})
export class SearchComponent implements OnInit {

  suggestResults$: Observable<SuggestResponse[]>;
  searchResults: Map<string, SearchResponse[]>;
  searchButtonClicked: boolean;

  private suggestTerms = new Subject<string>();

  constructor(private searchService: SearchService) {
  }

  ngOnInit() {
    this.searchResults = new Map<string, SearchResponse[]>();
    this.searchButtonClicked = false;

    this.suggestResults$ = this.suggestTerms.pipe(
      // wait 300ms after each keystroke before considering the term
      debounceTime(100),

      // ignore new term if same as previous term
      distinctUntilChanged(),

      // switch to new search observable each time the term changes
      switchMap((term: string) => this.searchService.suggest(term)),
    );
  }

  suggest(term: string): void {
    this.searchButtonClicked = false;
    this.suggestTerms.next(term);
  }

  search(term: string): void {
    this.searchResults.clear();
    this.searchButtonClicked = true;
    term = term.trim();
    if (!term) {
      return;
    }
    this.searchService.search(term)
      .subscribe((response: SearchResponse[]) => {
        response.forEach(res => {
          let list: SearchResponse[] = this.searchResults.get(res.version)
          if (!list) {
            list = [];
          }
          list.push(res);
          this.searchResults.set(res.version, list);
        });
      });
  }

  getKeys(): string[] {
    return Array.from(this.searchResults.keys());
  }

  // getPdfName(url: string): string {
  //   return url.substring(url.lastIndexOf('/') + 1, url.length);
  // }

}
